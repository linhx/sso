package com.linhx.sso.configs.security;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.User;
import com.linhx.sso.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author linhx
 * @since 08/10/2020
 */
@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        User user = null;
        try {
            user = this.userService.findByUsername(username).orElse(null);
        } catch (BaseException e) {
            e.printStackTrace();
        }
        if (user == null) {
            throw new UsernameNotFoundException("error.login.not-found");
        }
        // remove sensitive info
        UserDetail cloneUser = new UserDetail();
        cloneUser.setId(user.getId());
        cloneUser.setUuid(user.getUuid());
        cloneUser.setActive(user.isActive());

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
