package com.linhx.sso.controller;

import lombok.Getter;
import lombok.Setter;

/**
 * CaptchaSession
 *
 * @author linhx
 * @since 19/08/2021
 */
@Getter
@Setter
public class CaptchaSession {
    private String captchaLogin;

    public boolean compareAndInvalidateCaptchaLogin(String captchaLogin) {
        var cloneCaptchaLogin = this.captchaLogin;
        this.captchaLogin = null;
        return captchaLogin != null && captchaLogin.equals(cloneCaptchaLogin);
    }
}
