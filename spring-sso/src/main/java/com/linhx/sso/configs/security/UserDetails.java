package com.linhx.sso.configs.security;

import com.linhx.sso.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author linhx
 * @since 08/10/2020
 */
@Getter
public class UserDetails extends org.springframework.security.core.userdetails.User {
    private UserDetail user;

    public UserDetails(UserDetail user, String username, String password,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.user = user;
    }

    public UserDetails(UserDetail user, String username,
                       String password, boolean enabled, boolean accountNonExpired,
                       boolean credentialsNonExpired, boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }
}
