package com.linhx.sso.configs.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * RefreshTokenDetail
 *
 * @author linhx
 * @since 17/08/2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDetail {
    private Long id;
    private Long userId;
    private Date expired;
}
