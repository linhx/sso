package com.linhx.sso.controller;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.configs.security.TokenService;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.request.GrantAccessTokenDto;
import com.linhx.sso.entities.User;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.RequestAccessTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

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
    private final TokenService tokenService;
    private final RequestAccessTokenService requestAccessTokenService;
    private final AuthService authService;

    public AuthController(TokenService tokenService, RequestAccessTokenService requestAccessTokenService, AuthService authService) {
        this.tokenService = tokenService;
        this.requestAccessTokenService = requestAccessTokenService;
        this.authService = authService;
    }

    @GetMapping(Paths.LOGIN)
    public String loginPage() {
        return "login";
    }

    @GetMapping(Paths.ACCOUNT)
    public void account(@RequestParam("callbackUrl") String callbackUrl,
                        HttpServletResponse response,
                        @AuthenticationPrincipal User principal) throws Exception {
        var callback = new URL(callbackUrl);
        var signInUrl = this.requestAccessTokenService.createRequestAccessTokenUrl(principal, callback);
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
}
