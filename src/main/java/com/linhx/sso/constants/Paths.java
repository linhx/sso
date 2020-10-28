package com.linhx.sso.constants;

public class Paths {
    private Paths() {
        throw new IllegalStateException("Can't create instance of Constant class");
    }

    public static final String LOGIN = "/login";

    public static final String ACCOUNT = "/account";


    public static final String[] PUBLIC_PATHS = {
            LOGIN,
            // swagger
            "/swagger-ui.html",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };
}
