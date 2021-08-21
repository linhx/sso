package com.linhx.sso.services.loginattempt;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.LoginAttempt;

import java.util.Optional;

/**
 * LoginAttemptService
 *
 * @author linhx
 * @since 21/08/2021
 */
public interface LoginAttemptService {
    Optional<LoginAttempt> findByIp(String ip);
    boolean check(String ip);
    LoginAttempt attemptFailed(String ip) throws BaseException;
}
