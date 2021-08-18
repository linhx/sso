package com.linhx.sso.constants;

public class SecurityConstants {
    private SecurityConstants () {
        throw new IllegalStateException("Constant class");
    }

    /*
     * JWT
     */
    public static final String JWT_USER = "user";
    public static final String JWT_ID = "id";
    public static final String JWT_USERNAME = "username";
    public static final String JWT_REFRESH_TOKEN_ID = "rt_id";
    public static final String JWT_LOGIN_HISTORY_ID = "lh";

    public static final int TOKEN_EXPIRATION_SECONDS = 30 * 60; // 30 minutes
    public static final int REQUEST_TOKEN_EXPIRATION_SECONDS = 10 * 60; // 3 minutes
    public static final int REFRESH_TOKEN_EXPIRATION_SECONDS = 7 * 24 * 60 * 60; // 7 days

    public static final String COOKIE_ACCESS_TOKEN = "at";
    public static final String COOKIE_REFRESH_TOKEN = "rt";
}
