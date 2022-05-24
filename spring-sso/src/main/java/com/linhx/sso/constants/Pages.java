package com.linhx.sso.constants;

/**
 * Pages
 *
 * @author linhx
 * @since 17/08/2021
 */
public class Pages {
    private Pages () {
        throw new IllegalStateException("Constant class");
    }

    public static final String LOGIN = "login";
    public static final String FORGOT_PASSWORD = "forgot-password";
    public static final String RESET_PASSWORD = "reset-password";
    public static final String HOME = "index";
}
