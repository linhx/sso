package com.linhx.sso.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Sessions
 *
 * @author linhx
 * @since 19/08/2021
 */
@Component
public class Sessions {
    @Bean
    @SessionScope
    public CaptchaSession captchaSession() {
        return new CaptchaSession();
    }
}
