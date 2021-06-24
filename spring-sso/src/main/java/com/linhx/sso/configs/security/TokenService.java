package com.linhx.sso.configs.security;

import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.User;
import com.linhx.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linhx
 * @since 08/10/2020
 */
@Service
public class TokenService {
    @Autowired
    private EnvironmentVariable env;

    public JwtUtils.JwtResult generateAccessToken(User user) throws Exception {
        return JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_ID, user.getUuid()),
                this.env.getAccessTokenSecret(),
                SecurityConstants.TOKEN_EXPIRATION_SECONDS);
    }

    public JwtUtils.JwtResult generateRefreshToken(User user) throws Exception {
        return JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_ID, user.getUuid()),
                this.env.getRefreshTokenSecret(),
                SecurityConstants.REFRESH_TOKEN_EXPIRATION_SECONDS);
    }

    public User parseAccessToken(String token) {
        Claims claims = JwtUtils.parse(token, this.env.getAccessTokenSecret());
        var id = claims.get(SecurityConstants.JWT_ID, String.class);
        var user = new User();
        user.setUuid(id);
        return user;
    }

    public User parseRefreshToken(String token) {
        Claims claims = JwtUtils.parse(token, this.env.getRefreshTokenSecret());
        var id = claims.get(SecurityConstants.JWT_ID, String.class);
        var user = new User();
        user.setUuid(id);
        return user;
    }

    /**
     * Gets the authorities.
     *
     * @return the authorities
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(new ArrayList<>()); // TODO
    }

    /**
     * Gets the granted authorities.
     *
     * @param privileges the privileges
     * @return the granted authorities
     */
    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
