package com.linhx.sso.controller.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RequestAccessTokenDto
 *
 * @author linhx
 * @since 08/11/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GrantAccessTokenDto {
    private String clientId;
    private String clientSecret;
    private String rat;
}
