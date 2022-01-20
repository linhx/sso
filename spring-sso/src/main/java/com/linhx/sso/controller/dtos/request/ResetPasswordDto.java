package com.linhx.sso.controller.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    @NotNull
    @Valid
    private CaptchaReqDto captcha;
}
