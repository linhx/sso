package com.linhx.sso.controller.dtos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * CaptchaDto
 *
 * @author linhx
 * @since 26/11/2021
 */
@Getter
@Setter
@NoArgsConstructor
public class CaptchaReqDto {
    @NotEmpty
    private String id;
    @NotEmpty
    private String value;
}
