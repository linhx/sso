package com.linhx.sso.repositories;

import com.linhx.sso.entities.LoginAttempt;

import java.util.Optional;

/**
 * LoginAttemptRepository
 *
 * @author linhx
 * @since 21/08/2021
 */
public interface LoginAttemptRepository {
    LoginAttempt save(LoginAttempt loginAttempt);
    void delete(LoginAttempt loginAttempt);
    Optional<LoginAttempt> findByIp(String ip);
}
