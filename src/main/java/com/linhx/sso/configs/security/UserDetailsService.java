package com.linhx.sso.configs.security;

import com.linhx.sso.entities.User;
import com.linhx.sso.services.IUserService;
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
    private IUserService userService;
    @Autowired
    private TokenService tokenService;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = null;
        user = this.userService.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("error.login.not-found");
        }
        // remove sensitive info
        User cloneUser = new User();
        cloneUser.setId(user.getId());
        cloneUser.setActive(user.isActive());

        return new UserDetails(cloneUser,
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                this.tokenService.getAuthorities());
    }
}
