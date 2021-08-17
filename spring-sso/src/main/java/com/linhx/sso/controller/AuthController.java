package com.linhx.sso.controller;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.Pages;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.request.GrantAccessTokenDto;
import com.linhx.sso.entities.User;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.RequestAccessTokenService;
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
    private final RequestAccessTokenService requestAccessTokenService;
    private final AuthService authService;
    private final EnvironmentVariable env;

    public AuthController(RequestAccessTokenService requestAccessTokenService,
                          AuthService authService, EnvironmentVariable env) {
        this.requestAccessTokenService = requestAccessTokenService;
        this.authService = authService;
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
                               HttpServletResponse response)
            throws BaseException, AuthenticationException {
        var token = this.authService.refresh(refreshToken);

        var cookieAccessToken = new Cookie(SecurityConstants.COOKIE_ACCESS_TOKEN, token.getAccessToken());
        cookieAccessToken.setHttpOnly(true);
        cookieAccessToken.setDomain(this.env.getSecurityDomain());
        var cookieRefreshToken = new Cookie(SecurityConstants.COOKIE_REFRESH_TOKEN, token.getRefreshToken());
        cookieRefreshToken.setHttpOnly(true);
        cookieRefreshToken.setDomain(this.env.getSecurityDomain());

        response.addCookie(cookieAccessToken);
        response.addCookie(cookieRefreshToken);
    }
}
