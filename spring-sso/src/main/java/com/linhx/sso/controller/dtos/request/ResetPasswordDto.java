package com.linhx.sso.controller.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

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
    @NotBlank
    private String password;
    @NotBlank
    private String rePassword;
    @NotBlank
    private String captcha;
}
