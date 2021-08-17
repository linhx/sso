package com.linhx.sso.configs.security;

import com.linhx.sso.entities.User;
import lombok.*;

/**
 * UserDetail
 *
 * @author linhx
 * @since 17/08/2021
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PACKAGE)
public class UserDetail {
    private Long id;
    private String uuid;
    private String username;
    private boolean isActive;

    public static UserDetail fromEntity(User user) {
        return new UserDetail(user.getId(), user.getUuid(), user.getUsername(), user.isActive());
    }
}
