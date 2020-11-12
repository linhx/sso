package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User
 *
 * @author linhx
 * @since 28/10/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String uuid;
    private String username;
    private String password;
    private boolean isActive;
}
