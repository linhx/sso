package com.linhx.sso.controller;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.ResetPasswordException;
import com.linhx.exceptions.message.Message;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.security.UserDetail;
import com.linhx.sso.constants.Pages;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.*;
import com.linhx.sso.entities.User;
import com.linhx.sso.exceptions.BadRequestException;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.sso.exceptions.RefreshTokenAlreadyUsedException;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.CaptchaService;
import com.linhx.sso.services.RequestAccessTokenService;
import com.linhx.sso.services.UserService;
import com.linhx.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
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
    private final CaptchaService captchaService;
    private final EnvironmentVariable env;

    public AuthController(RequestAccessTokenService requestAccessTokenService,
                          AuthService authService, UserService userService,
                          CaptchaService captchaService,
                          EnvironmentVariable env) {
        this.requestAccessTokenService = requestAccessTokenService;
        this.authService = authService;
        this.userService = userService;
        this.captchaService = captchaService;
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
            cookieAccessToken.setMaxAge((int) ((token.getAccessTokenExpired().getTime() - System.currentTimeMillis()) / 1000));
            var cookieRefreshToken = new Cookie(SecurityConstants.COOKIE_REFRESH_TOKEN, token.getRefreshToken());
            cookieRefreshToken.setHttpOnly(true);
            cookieRefreshToken.setDomain(this.env.getSecurityDomain());
            cookieRefreshToken.setMaxAge((int) ((token.getRefreshTokenExpired().getTime() - System.currentTimeMillis()) / 1000));
            cookieRefreshToken.setPath(Paths.REFRESH_TOKEN);

            response.addCookie(cookieAccessToken);
            response.addCookie(cookieRefreshToken);
        } catch (RefreshTokenAlreadyUsedException e) {
            // if a refresh token is already used, that means maybe the refresh token was stolen.
            // so invalidate the refresh token and the access token by adding a logout scheduler by user login history id.
            // For the case two tab of one browser refresh token at the same time => one of them will fail (RefreshTokenAlreadyUsedException)
            // if user can verify that the two request is from on browser then cancel the scheduler
            // using a jwt (contains the scheduler id) in the cookie to verify, check com.linhx.sso.configs.security.AuthorizationFilter.cancelLogoutByLoginHistoryScheduler
            var logoutByLoginHistoryScheduler = this.authService.logoutScheduler(e.getLoginHistoryId());
            var llhsJwt = this.authService.createLlhsJwt(logoutByLoginHistoryScheduler.getId());
            throw new LoginInfoWrongException(Message.error("error.refreshToken.alreadyUsed")
                    .param(SecurityConstants.LOGOUT_BY_LH_SCHEDULER_ID, llhsJwt).build());
        }
    }

    @PostMapping(Paths.CANCEL_LOGOUT)
    public void cancelLogout(@RequestBody CancelLogoutDto dto) {
        if (dto != null && StringUtils.isExist(dto.getLlhs())) {
            var llhsId = this.authService.parseLlhsJwt(dto.getLlhs());
            this.authService.cancelLogoutByLoginHistoryScheduler(llhsId);
        } else {
            throw new LoginInfoWrongException("error.refreshToken.cancelLogout.missingToken");
        }
    }


    @GetMapping(Paths.PROFILE)
    @ResponseBody
    public Object getProfile(@AuthenticationPrincipal UserDetail principal) {
        return principal;
    }

    @GetMapping(Paths.FORGOT_PASSWORD)
    public String forgotPasswordPage() {
        return Pages.FORGOT_PASSWORD;
    }

    @PostMapping(Paths.FORGOT_PASSWORD)
    @ResponseBody
    public void forgotPassword(@RequestBody @Validated ResetPasswordRequestDto resetPasswordRequestDto) throws BaseException {
        if (!this.captchaService.isValid(resetPasswordRequestDto.getCaptcha())) {
            throw new BadRequestException("error.forgotPassword.invalidCaptcha");
        }
        try {
            this.userService.requestResetPassword(resetPasswordRequestDto);
        } catch (ResetPasswordException e) {
            throw e;
        } catch (BaseException e) {
            logger.error("error.requestResetPassword", e);
        }
    }

    @GetMapping(Paths.RESET_PASSWORD + "/{token}")
    public String resetPasswordPage() {
        return Pages.RESET_PASSWORD;
    }

    @PostMapping(Paths.RESET_PASSWORD + "/{token}")
    @ResponseBody
    public void resetPassword(@RequestBody @Validated ResetPasswordDto dto, @PathVariable("token") String token)
            throws BaseException {
        if (!this.captchaService.isValid(dto.getCaptcha())) {
            throw new BadRequestException("error.resetPassword.invalidCaptcha");
        }
        this.userService.resetPassword(dto, token);
    }
}
