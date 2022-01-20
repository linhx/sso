package com.linhx.sso.controller.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ResetPassword
 *
 * @author linhx
 * @date 08/04/2020
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {
    /**
     * Username or email
     */
    @NotBlank
    private String identifier;

    /**
     * captcha
     */
    @NotNull
    @Valid
    private CaptchaReqDto captcha;
}
