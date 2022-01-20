package com.linhx.sso.controller;

import com.linhx.sso.constants.Paths;
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
    private final CaptchaSession captchaSession;

    public CaptchaController(CaptchaSession captchaSession) {
        this.captchaSession = captchaSession;
    }

    @GetMapping(Paths.CAPTCHA_LOGIN)
    @ResponseBody
    public Object getCaptchaLogin() {
        return this.captchaSession.generateCaptchaLogin();
    }

    @GetMapping(Paths.CAPTCHA_FORGOT_PASSWORD)
    @ResponseBody
    public Object getCaptchaForgotPassword() {
        return this.captchaSession.generateCaptchaForgotPassword();
    }

    @GetMapping(Paths.CAPTCHA_RESET_PASSWORD)
    @ResponseBody
    public Object getCaptchaResetPassword() {
        return this.captchaSession.generateCaptchaResetPassword();
    }
}
