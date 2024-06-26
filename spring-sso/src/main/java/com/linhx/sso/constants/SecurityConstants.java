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
    public static final String JWT_LOGOUT_BY_LOGIN_HIS_SCHEDULER_ID = "llhs";

    public static final int TOKEN_EXPIRATION_SECONDS = 30 * 60; // 30 minutes
    public static final int REQUEST_TOKEN_EXPIRATION_SECONDS = 10 * 60; // 3 minutes
    public static final int REFRESH_TOKEN_EXPIRATION_SECONDS = 7 * 24 * 60 * 60; // 7 days

    /**
     * Reset password
     */
    public static final Integer MAX_GENERATE_RESET_PASSWORD_TOKEN_TIMES = 5;
    public static final int RESET_PASSWORD_TOKEN_EXPIRED_TIME = 60 * 30; // 30 minutes

    public static final String COOKIE_ACCESS_TOKEN = "at";
    public static final String COOKIE_REFRESH_TOKEN = "rt";
    public static final String LOGOUT_BY_LH_SCHEDULER_ID = "llhs";
}
