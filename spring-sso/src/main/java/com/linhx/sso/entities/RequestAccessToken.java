package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * RequestAccessToken
 *
 * @author linhx
 * @since 08/11/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class RequestAccessToken extends Base {
    @Transient
    public static final String SEQ_NAME = "RequestAccessToken";

    @Id
    private Long id;
    @Indexed(unique = true)
    private String uuid;
    private Long userId;
    private Long clientApplicationId;
    private Date expired;
    private boolean isValid;
}
