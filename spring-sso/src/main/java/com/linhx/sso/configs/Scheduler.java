package com.linhx.sso.configs;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.services.RequestAccessTokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduler
 *
 * @author linhx
 * @since 12/11/2020
 */
@Configuration
public class Scheduler {
    private final RequestAccessTokenService requestAccessTokenService;

    public Scheduler(RequestAccessTokenService requestAccessTokenService) {
        this.requestAccessTokenService = requestAccessTokenService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteInvalidRequestAccessToken() throws BaseException {
        this.requestAccessTokenService.deleteInvalidRequestsAccessToken();
    }
}
