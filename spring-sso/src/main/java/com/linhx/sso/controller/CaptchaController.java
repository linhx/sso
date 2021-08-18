package com.linhx.sso.controller;

import com.github.cage.Cage;
import com.linhx.sso.constants.Paths;
import com.linhx.sso.utils.CCage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;

/**
 * CaptchaController
 *
 * @author linhx
 * @since 19/08/2021
 */
@Controller
public class CaptchaController {
    private static final Cage cage = new CCage();
    private final CaptchaSession captchaSession;

    public CaptchaController(CaptchaSession captchaSession) {
        this.captchaSession = captchaSession;
    }

    @GetMapping(Paths.CAPTCHA_LOGIN)
    @ResponseBody
    public Object getCaptchaLogin() {
        var token = cage.getTokenGenerator().next();
        this.captchaSession.setCaptchaLogin(token);
        var img = cage.draw(token);
        return Base64.getEncoder().encode(img);
    }
}
