package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * LoginHistory
 *
 * @author linhx
 * @since 18/08/2021
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class LoginHistory extends Base {
    @Transient
    public static final String SEQ_NAME = LoginHistory.class.getName();

    @Id
    private Long id;

    private Long userId;
}
