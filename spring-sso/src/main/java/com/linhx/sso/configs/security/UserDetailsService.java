package com.linhx.sso.configs.security;

import com.linhx.sso.services.UserService;
import com.linhx.sso.services.token.TokenService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author linhx
 * @since 08/10/2020
 */
@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;
    private final TokenService tokenService;

    public UserDetailsService(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        var user = this.userService.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("error.login.not-found");
        }
        // remove sensitive info
        var cloneUser = UserDetail.fromEntity(user);

        return new UserDetails(cloneUser,
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                this.tokenService.getAuthorities(cloneUser));
    }
}
