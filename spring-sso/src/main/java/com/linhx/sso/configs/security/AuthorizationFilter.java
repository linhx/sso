package com.linhx.sso.configs.security;

import com.linhx.sso.constants.Paths;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.services.token.TokenService;
import com.linhx.utils.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger cLogger = LoggerFactory.getLogger(AuthorizationFilter.class);
    private final TokenService tokenService;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               TokenService tokenService) {
        super(authenticationManager);
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(request);
        if (!Objects.isNull(authentication)) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        String accessToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SecurityConstants.COOKIE_ACCESS_TOKEN.equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }
        if (StringUtils.isEmpty(accessToken)) return null;
        try {
            var tokenDetails = this.tokenService.parseAccessToken(accessToken);
            var isInvalid = this.tokenService.isInvalid(tokenDetails.getId());
            if (isInvalid) {
                cLogger.warn("Blocked token {}", tokenDetails.getId());
            } else {
                return new UsernamePasswordAuthenticationToken(tokenDetails.getUser(),
                        null,
                        this.tokenService.getAuthorities(tokenDetails.getUser()));
            }
        } catch (ExpiredJwtException e) {
            cLogger.warn("Request to parse expired JWT: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            cLogger.warn("Request to parse unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            cLogger.warn("Request to parse invalid JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            cLogger.warn("Request to parse WT with invalid signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            cLogger.warn("Request to parse empty or null JWT: {}", e.getMessage());
        } catch (Exception e) {
            cLogger.warn("Wrong jwt: {}", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        if (Paths.ACCOUNT.equals(request.getServletPath())) {
            response.sendRedirect(Paths.LOGIN); // TODO callback url
        } else {
            super.onUnsuccessfulAuthentication(request, response, failed);
        }
    }
}
