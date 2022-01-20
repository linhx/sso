package com.linhx.sso.services;

import com.linhx.sso.controller.dtos.request.CaptchaReqDto;
import com.linhx.sso.controller.dtos.response.CaptchaDto;

/**
 * CaptchaService
 *
 * @author linhx
 * @since 26/11/2021
 */
public interface CaptchaService {
    CaptchaDto create();
    boolean isValid(CaptchaReqDto captcha);
}
