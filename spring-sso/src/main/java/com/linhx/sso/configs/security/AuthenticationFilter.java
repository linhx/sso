package com.linhx.sso.configs.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.message.Message;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.CaptchaSession;
import com.linhx.sso.controller.dtos.response.MessagesDto;
import com.linhx.sso.exceptions.InvalidLoginCaptchaException;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.sso.services.UserService;
import com.linhx.sso.services.loginattempt.LoginAttemptService;
import com.linhx.sso.services.token.TokenService;
import com.linhx.sso.utils.HttpUtils;
import com.linhx.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final TokenService tokenService;
    private final EnvironmentVariable env;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;
    private final CaptchaSession captchaSession;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                TokenService tokenService,
                                UserService userService,
                                EnvironmentVariable env, LoginAttemptService loginAttemptService, CaptchaSession captchaSession) {
        this.loginAttemptService = loginAttemptService;
        this.captchaSession = captchaSession;
        this.setAuthenticationManager(authenticationManager);
        this.tokenService = tokenService;
        this.userService = userService;
        this.env = env;
    }

    private LoginInfo obtainLoginInfo(HttpServletRequest request) {
        try {
            var loginInfo = objectMapper.readValue(request.getInputStream(), LoginInfo.class);
            String username = loginInfo.getUsername();
            String password = loginInfo.getPassword();
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            loginInfo.setUsername(username);
            loginInfo.setPassword(password);
            return loginInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new LoginInfoWrongException("error.login.cantObtainLoginInfo");
    }

    private void checkCaptcha(String captcha) {
        var validCaptcha = this.captchaSession.compareAndInvalidateCaptchaLogin(captcha);
        if (!validCaptcha) {
            throw new InvalidLoginCaptchaException("error.auth.invalidCaptcha");
        }
    }

    private void checkExceededLoginAttempt(String ip) {
        var valid = this.loginAttemptService.check(ip);
        if (!valid) {
            throw new LoginInfoWrongException("error.login.loginFailTooMuch");
        }
    }

    public Authentication authentication(HttpServletRequest request) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        var loginInfo = this.obtainLoginInfo(request);

        // check exceeded login attempt fail
        this.checkExceededLoginAttempt(HttpUtils.getClientIp(request));

        // check captcha
        this.checkCaptcha(loginInfo.getCaptcha());

        // authenticate
        var authRequest = new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword());
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return this.authentication(request);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        try {
            var userDetails = (UserDetails) authResult.getPrincipal();
            var user = userDetails.getUser();
            var history = this.userService.createLoginHistory(user.getId());
            JwtUtils.JwtResult refreshTokenResult = this.tokenService.generateRefreshToken(userDetails.getUser(),
                    history.getId());
            JwtUtils.JwtResult accessTokenResult = this.tokenService.generateAccessToken(userDetails.getUser(),
                    history.getId(),
                    refreshTokenResult.getTokenId());

            var cookieAccessToken = new Cookie(SecurityConstants.COOKIE_ACCESS_TOKEN, accessTokenResult.getToken());
            cookieAccessToken.setHttpOnly(true);
            cookieAccessToken.setDomain(this.env.getSecurityDomain());
            var cookieRefreshToken = new Cookie(SecurityConstants.COOKIE_REFRESH_TOKEN, refreshTokenResult.getToken());
            cookieRefreshToken.setHttpOnly(true);
            cookieRefreshToken.setDomain(this.env.getSecurityDomain());

            response.addCookie(cookieAccessToken);
            response.addCookie(cookieRefreshToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException {
        try {
            this.loginAttemptService.attemptFailed(HttpUtils.getClientIp(request));
        } catch (BaseException e) {
            logger.error("Can't save login attempt fail");
        }

        SecurityContextHolder.clearContext();
        String msg;
        if (failed instanceof LockedException) {
            msg = "error.login.locked-account";
        } else if (failed instanceof DisabledException) {
            msg = "error.login.disabled-account";
        } else if (failed instanceof InvalidLoginCaptchaException) {
            msg = "error.login.invalid-captcha";
        } else if (failed instanceof LoginInfoWrongException) {
            msg = failed.getMessage();
        } else {
            msg = "error.login.cant-login";
        }

        var errorMsg = Message.error(msg).build();
        var tokensStr = this.objectMapper.writeValueAsString(MessagesDto.fromMessage(errorMsg));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(tokensStr);
        out.flush();
    }
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class LoginInfo {
    private String username;
    private String password;
    private String captcha;
}
