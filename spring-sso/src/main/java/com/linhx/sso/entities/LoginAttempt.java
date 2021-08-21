package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * LoginAttempt
 *
 * @author linhx
 * @since 21/08/2021
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class LoginAttempt extends Base {
    @Transient
    public static final String SEQ_NAME = Token.class.getName();

    @Id
    private Long id;

    private String ip;
    private Integer times;
    private Date at;
}
