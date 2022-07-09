package com.linhx.sso.configs.security;

import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.services.token.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LogoutHandler
 *
 * @author linhx
 * @since 24/05/2022
 */
public class LogoutHandler extends SecurityContextLogoutHandler {
    private TokenService tokenService;
    private EnvironmentVariable env;

    public LogoutHandler(TokenService tokenService, EnvironmentVariable env) {
        this.tokenService = tokenService;
        this.env = env;
    }

    private void clearCookie(String cookie, String path, HttpServletRequest request, HttpServletResponse response) {
        Cookie clearCookie = new Cookie(cookie, null);
        String contextPath = request.getContextPath();
        String cookiePath = StringUtils.hasText(path) ? path : contextPath;
        clearCookie.setPath(cookiePath);
        clearCookie.setMaxAge(0);
        clearCookie.setDomain(this.env.getSecurityDomain());
        clearCookie.setSecure(request.isSecure());
        response.addCookie(clearCookie);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SecurityConstants.COOKIE_ACCESS_TOKEN.equals(cookie.getName())) {
                    // invalidate access token
                    this.tokenService.invalidateByAccessToken(cookie.getValue());
                    this.clearCookie(SecurityConstants.COOKIE_ACCESS_TOKEN, Paths.ROOT, request, response);
                    this.clearCookie(SecurityConstants.COOKIE_REFRESH_TOKEN, Paths.REFRESH_TOKEN, request, response);
                }
            }
        }
    }
}
