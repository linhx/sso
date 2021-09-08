package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document
public class User extends Base {
    @Transient
    public static final String SEQ_NAME = User.class.getName();

    @Id
    private Long id;
    @Indexed(unique = true)
    private String uuid;
    @Indexed(unique = true)
    private String username;
    private String password;
    @Indexed(unique = true)
    private String email;
    private boolean isActive;
}
