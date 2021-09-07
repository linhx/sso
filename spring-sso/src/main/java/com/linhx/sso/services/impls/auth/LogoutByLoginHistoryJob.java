package com.linhx.sso.services.impls.auth;

import com.linhx.sso.services.token.TokenService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * scheduler job for invalidate all token of a user by login history id
 *
 * @author linhx
 * @since 06/09/2021
 */
@Component
public class LogoutByLoginHistoryJob implements Job {
    /**
     * use Autowired instead of constructor because of Quartz can init a job that have parameter in constructor
     */
    @Autowired
    private TokenService tokenService;
    /**
     * Login history ID
     */
    public static final String DATA_KEY_LH_ID = "lh_id";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long loginHistoryId = (Long) context.getMergedJobDataMap().get(DATA_KEY_LH_ID);
        this.tokenService.invalidateByLoginHistoryId(loginHistoryId);
    }
}
