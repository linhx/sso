package com.linhx.sso.controller.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * CaptchaDto
 *
 * @author linhx
 * @since 26/11/2021
 */
@Getter
@Setter
@AllArgsConstructor
public class CaptchaDto {
    private String id;
    private String image;
}
