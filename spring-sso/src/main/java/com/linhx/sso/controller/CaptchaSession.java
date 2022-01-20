package com.linhx.sso.controller;

import com.linhx.sso.utils.CCage;
import com.linhx.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

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
    private String captchaForgotPassword;
    private String captchaResetPassword;

    public byte[] generateCaptchaLogin() {
        this.captchaLogin = CCage.generateToken();
        return CCage.drawToBase64(this.captchaLogin);
    }

    public byte[] generateCaptchaForgotPassword() {
        this.captchaForgotPassword = CCage.generateToken();
        return CCage.drawToBase64(this.captchaForgotPassword);
    }

    public byte[] generateCaptchaResetPassword() {
        this.captchaResetPassword = CCage.generateToken();
        return CCage.drawToBase64(this.captchaResetPassword);
    }

    public boolean compareAndInvalidateCaptchaLogin(String captchaLogin) {
        var cloneCaptchaLogin = this.captchaLogin;
        this.captchaLogin = null;
        return StringUtils.isExist(captchaLogin) && captchaLogin.equals(cloneCaptchaLogin);
    }

    public boolean compareAndInvalidateCaptchaForgotPassword(String captchaForgotPassword) {
        var cloneCaptchaForgotPassword = this.captchaForgotPassword;
        this.captchaForgotPassword = null;
        return StringUtils.isExist(captchaForgotPassword) && captchaForgotPassword.equals(cloneCaptchaForgotPassword);
    }

    public boolean compareAndInvalidateCaptchaResetPassword(String captchaResetPassword) {
        var cloneCaptchaResetPassword = this.captchaResetPassword;
        this.captchaResetPassword = null;
        return StringUtils.isExist(captchaResetPassword) && captchaResetPassword.equals(cloneCaptchaResetPassword);
    }
}
