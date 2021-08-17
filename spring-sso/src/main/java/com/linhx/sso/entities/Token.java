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
 * Token
 *
 * @author linhx
 * @since 17/08/2021
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class Token extends Base {
    @Transient
    public static final String SEQ_NAME = Token.class.getName();

    @Id
    private Long id;

    private Long userId;

    private Date expired;

    private boolean isInvalid;
}
