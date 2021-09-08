package com.linhx.sso.controller.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ResetPasswordDto
 *
 * @author linhx
 * @date 11/04/2020
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    private String token;
    private String password;
    private String rePassword;
}