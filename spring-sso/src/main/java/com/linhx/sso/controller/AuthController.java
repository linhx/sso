package com.linhx.sso.controller;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.ResetPasswordException;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.security.UserDetail;
import com.linhx.sso.constants.Pages;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.request.GrantAccessTokenDto;
import com.linhx.sso.controller.dtos.request.ResetPasswordDto;
import com.linhx.sso.controller.dtos.request.ResetPasswordRequestDto;
import com.linhx.sso.entities.User;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.sso.exceptions.RefreshTokenAlreadyUsedException;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.RequestAccessTokenService;
import com.linhx.sso.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * AuthController
 *
 * @author linhx
 * @since 28/10/2020
 */
@Controller
@RequestMapping
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final RequestAccessTokenService requestAccessTokenService;
    private final AuthService authService;
    private final UserService userService;
    private final EnvironmentVariable env;

    public AuthController(RequestAccessTokenService requestAccessTokenService,
                          AuthService authService, UserService userService, EnvironmentVariable env) {
        this.requestAccessTokenService = requestAccessTokenService;
        this.authService = authService;
        this.userService = userService;
        this.env = env;
    }

    @GetMapping(Paths.LOGIN)
    public String loginPage() {
        return Pages.LOGIN;
    }

    @GetMapping(Paths.ACCOUNT)
    public void account(@RequestParam("callbackUrl") String callbackUrl,
                        HttpServletResponse response,
                        @AuthenticationPrincipal User principal) throws Exception {
        var signInUrl = this.requestAccessTokenService.createRequestAccessTokenUrl(principal, callbackUrl);
        response.sendRedirect(signInUrl);
    }

    @PostMapping(Paths.GRANT_TOKEN)
    @ResponseBody
    public Object grantToken(@RequestBody GrantAccessTokenDto requestAccessToken) throws BaseException {
        return this.requestAccessTokenService.grantAccessToken(requestAccessToken);
    }

    @PostMapping(Paths.AUTH)
    @ResponseBody
    public Object auth(@RequestBody AuthDto dto) throws AuthenticationException, BaseException {
        return this.authService.auth(dto);
    }

    @PostMapping(Paths.REFRESH_TOKEN)
    @ResponseBody
    public void refreshToken(@CookieValue(SecurityConstants.COOKIE_REFRESH_TOKEN) String refreshToken,
                             HttpServletResponse response) throws Exception {
        try {
            var token = this.authService.refresh(refreshToken);
            var cookieAccessToken = new Cookie(SecurityConstants.COOKIE_ACCESS_TOKEN, token.getAccessToken());
            cookieAccessToken.setHttpOnly(true);
            cookieAccessToken.setDomain(this.env.getSecurityDomain());
            var cookieRefreshToken = new Cookie(SecurityConstants.COOKIE_REFRESH_TOKEN, token.getRefreshToken());
            cookieRefreshToken.setHttpOnly(true);
            cookieRefreshToken.setDomain(this.env.getSecurityDomain());

            response.addCookie(cookieAccessToken);
            response.addCookie(cookieRefreshToken);
        } catch (RefreshTokenAlreadyUsedException e) {
            // for the case two tab of one browser refresh token at the same time => one of them will fail (RefreshTokenAlreadyUsedException)
            // add a logout scheduler by user login history id
            // if user can verify that the two request is from on browser then cancel the scheduler
            // using a jwt (contains the scheduler id) in the cookie to verify, check com.linhx.sso.configs.security.AuthorizationFilter.cancelLogoutByLoginHistoryScheduler
            var logoutByLoginHistoryScheduler = this.authService.logoutScheduler(e.getLoginHistoryId());
            var llhsJwt = this.authService.createLlhsJwt(logoutByLoginHistoryScheduler.getId());
            var cookieLogoutByLhScheduler = new Cookie(SecurityConstants.COOKIE_LOGOUT_BY_LH_SCHEDULER_ID, llhsJwt);
            cookieLogoutByLhScheduler.setHttpOnly(true);
            cookieLogoutByLhScheduler.setDomain(this.env.getSecurityDomain());
            response.addCookie(cookieLogoutByLhScheduler);
            throw new LoginInfoWrongException("error.refreshToken.alreadyUsed");
        }
    }

    @GetMapping(Paths.PROFILE)
    @ResponseBody
    public Object getProfile(@AuthenticationPrincipal UserDetail principal) {
        return principal;
    }


    @PostMapping(Paths.FORGET_PASSWORD)
    @ResponseBody
    public void forgetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) throws BaseException {
        try {
            this.userService.requestResetPassword(resetPasswordRequestDto);
        } catch (ResetPasswordException e) {
            throw e;
        } catch (BaseException e) {
            logger.error("error.requestResetPassword: {}", e.getMessages());
        }
    }

    @PostMapping(Paths.RESET_PASSWORD)
    @ResponseBody
    public void resetPassword(@RequestBody ResetPasswordDto dto) throws BaseException {
        this.userService.resetPassword(dto);
    }
}
