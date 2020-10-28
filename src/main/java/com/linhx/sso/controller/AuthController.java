package com.linhx.sso.controller;

import com.linhx.sso.configs.security.TokenService;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.User;
import com.linhx.utils.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthController
 *
 * @author linhx
 * @since 28/10/2020
 */
@RestController
@RequestMapping
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping(Paths.ACCOUNT)
    public void account(@CookieValue("at") String accessToken,
                        @RequestParam("callbackUrl") String callbackUrl,
                        HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(accessToken)) {
            response.sendRedirect("/login");
            return;
        }
        try {
            this.tokenService.parseAccessToken(accessToken);
            response.sendRedirect(callbackUrl);
        } catch (ExpiredJwtException e) {
            logger.warn("Request to parse expired JWT:", e);
        } catch (UnsupportedJwtException e) {
            logger.warn("Request to parse unsupported JWT:", e);
        } catch (MalformedJwtException e) {
            logger.warn("Request to parse invalid JWT:", e);
        } catch (SignatureException e) {
            logger.warn("Request to parse WT with invalid signature:", e);
        } catch (IllegalArgumentException e) {
            logger.warn("Request to parse empty or null JWT:", e);
        }
    }
}
