package com.linhx.sso.constants;

public class SecurityConstants {
    private SecurityConstants () {
        throw new IllegalStateException("Constant class");
    }

    /*
     * JWT
     */
    public static final String TOKEN_REQUEST_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String WS_TOKEN_HEADER = "X-Authorization";

    public static final String JWT_ID = "id";
    public static final String JWT_USERNAME = "username";
    public static final String JWT_PHONE_NUMBER = "phone_number";
    public static final String JWT_ROLE_ID = "role_id";
    public static final String JWT_PRIVILEGES = "privileges";
    public static final String JWT_RAT_UUID = "rat_id";

    public static final String REQUEST_ACCESS_TOKEN = "rat";

    public static final Integer TOKEN_EXPIRATION_SECONDS = 30 * 60; // 30 minutes
    public static final Integer REQUEST_TOKEN_EXPIRATION_SECONDS = 3 * 60; // 3 minutes
    public static final Integer REFRESH_TOKEN_EXPIRATION_SECONDS = 7 * 24 * 60 * 60; // 7 days

    public static final String COOKIE_ACCESS_TOKEN = "at";
    public static final String COOKIE_REFRESH_TOKEN = "rt";

    /**
     * Reset password
     */
    public static final Integer MAX_GENERATE_RESET_PASSWORD_TOKEN_TIME = 5;
    public static final long RESET_PASSWORD_TOKEN_EXPIRED_TIME = 1800; // 30 minutes
}
