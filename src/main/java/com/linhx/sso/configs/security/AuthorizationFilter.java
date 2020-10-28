package com.linhx.sso.configs.security;

import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.User;
import com.linhx.utils.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private final TokenService tokenService;

    public AuthorizationFilter(AuthenticationManager authenticationManager, TokenService tokenService) {
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

    private UsernamePasswordAuthenticationToken getAuthentication (HttpServletRequest request) {
        String fullToken = request.getHeader(SecurityConstants.TOKEN_REQUEST_HEADER);
        if (StringUtils.isEmpty(fullToken)) return null;
        String token = fullToken.replace(SecurityConstants.TOKEN_PREFIX, "");
        if (StringUtils.isEmpty(token)) return null;
        try {
            User user = this.tokenService.parseAccessToken(token);
            return new UsernamePasswordAuthenticationToken(user, null); // TODO authorities
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
        return null;
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        super.onUnsuccessfulAuthentication(request, response, failed);
    }
}
