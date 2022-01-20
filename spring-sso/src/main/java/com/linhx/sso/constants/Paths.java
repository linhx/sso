package com.linhx.sso.constants;

public class Paths {
    private Paths() {
        throw new IllegalStateException("Can't create instance of Constant class");
    }

    public static final String ROOT = "/";
    public static final String ASSETS = "/assets/**";
    public static final String FAVICON = "favicon.ico";
    public static final String LOGIN = "/login";

    public static final String ACCOUNT = "/account";
    public static final String GRANT_TOKEN = "/grant-token";
    public static final String AUTH = "/auth";
    public static final String REFRESH_TOKEN = "/refresh-token";
    public static final String CANCEL_LOGOUT = "/cancel-logout";
    public static final String CAPTCHA_LOGIN = "/captcha-login";
    public static final String CAPTCHA_FORGOT_PASSWORD = "/captcha-forgot-password";
    public static final String CAPTCHA_RESET_PASSWORD = "/captcha-reset-password";
    public static final String PROFILE = "/profile";
    public static final String FORGOT_PASSWORD = "/forgot-password";
    public static final String RESET_PASSWORD = "/reset-password";

    public static final String[] PUBLIC_PATHS = {
            ASSETS,
            FAVICON,
            LOGIN,
            AUTH,
            REFRESH_TOKEN,
            CAPTCHA_LOGIN,
            CAPTCHA_FORGOT_PASSWORD,
            CAPTCHA_RESET_PASSWORD,
            FORGOT_PASSWORD,
            RESET_PASSWORD,
            RESET_PASSWORD + "/*",
    };
}
