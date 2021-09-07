package com.linhx.sso.configs.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author linhx
 * @since 20/10/2020
 */
@Getter
@Setter
@AllArgsConstructor
public class Tokens {
    private String accessToken;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date accessTokenExpired;
    private String refreshToken;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date refreshTokenExpired;
}
