package com.linhx.sso.constants;

/**
 * Messages
 *
 * @author linhx
 * @since 26/01/2021
 */
public class Messages {
    private Messages () {
        throw new IllegalStateException("Constant class");
    }

    public static final String ERR_REFRESH_TOKEN_INVALID = "error.refreshToken.invalidJwt";
}
