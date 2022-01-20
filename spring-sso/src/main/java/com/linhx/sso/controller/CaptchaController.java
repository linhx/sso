package com.linhx.sso.controller;

import com.linhx.sso.constants.Paths;
import com.linhx.sso.services.CaptchaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * CaptchaController
 *
 * @author linhx
 * @since 19/08/2021
 */
@Controller
public class CaptchaController {
    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping(Paths.CAPTCHA)
    @ResponseBody
    public Object getCaptcha() {
        return this.captchaService.create();
    }
}
