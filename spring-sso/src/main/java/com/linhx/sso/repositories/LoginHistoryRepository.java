package com.linhx.sso.repositories;

import com.linhx.sso.entities.LoginHistory;

/**
 * LoginHistory
 *
 * @author linhx
 * @since 18/08/2021
 */
public interface LoginHistoryRepository {
    LoginHistory save(LoginHistory loginHistory);
    void deleteAllById(Iterable<? extends Long> ids);
}
