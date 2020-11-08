package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class RequestAccessToken {
    private Long id;
    private String uuid;
    private Long userId;
    private Long clientApplicationId;
    private Date expired;
    private boolean isValid;
}
