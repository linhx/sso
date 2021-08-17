package com.linhx.sso.configs.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * TokenDetail
 *
 * @author linhx
 * @since 17/08/2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class TokenDetail {
    private Long id;
    /**
     * refresh token id
     */
    private Long rtId;
    private UserDetail user;
    private Date expired;
}
