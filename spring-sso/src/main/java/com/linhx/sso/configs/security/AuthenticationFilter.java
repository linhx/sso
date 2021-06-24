package com.linhx.sso.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhx.exceptions.message.Message;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.response.MessagesDto;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private final TokenService tokenService;
    private final EnvironmentVariable env;
    private final boolean postOnly = true;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                TokenService tokenService,
                                EnvironmentVariable env) {
        this.setAuthenticationManager(authenticationManager);
        this.tokenService = tokenService;
        this.env = env;
    }

    private LoginInfo obtainLoginInfo(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), LoginInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new LoginInfoWrongException("Can not extract login info (username, password)!");
    }

    public Authentication authentication(HttpServletRequest request) {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        var loginInfo = this.obtainLoginInfo(request);

        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        var authRequest = new UsernamePasswordAuthenticationToken(
                username, password);

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
            JwtUtils.JwtResult accessTokenResult = this.tokenService.generateAccessToken(userDetails.getUser());
            JwtUtils.JwtResult refreshTokenResult = this.tokenService.generateRefreshToken(userDetails.getUser());

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
        SecurityContextHolder.clearContext();
        String msg;
        if (failed instanceof LockedException) {
            msg = "error.login.locked-account";
        } else if (failed instanceof DisabledException) {
            msg = "error.login.disabled-account";
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
class LoginInfo {
    private String username;
    private String password;
}
