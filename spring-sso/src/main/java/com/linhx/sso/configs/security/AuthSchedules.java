package com.linhx.sso.configs.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for authentication: delete expired tokens;
 *
 * @author linhx
 * @since 18/08/2021
 */
@Component
public class AuthSchedules {
    private static final Logger logger = LoggerFactory.getLogger(AuthSchedules.class);

    private final TokenService tokenService;

    public AuthSchedules(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Delete expired login fcm tokens
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void deleteExpiredTokens() {
        try {
            this.tokenService.deleteExpiredTokens();
        } catch (Exception e) {
            logger.error("error.auth.cantDeleteExpiredToken", e);
        }
    }
}
