package com.linhx.sso.repositories;

import com.linhx.sso.entities.LogoutByLoginHistoryScheduler;

/**
 * LogoutByLoginHistorySchedulerRepository
 *
 * @author linhx
 * @since 07/09/2021
 */
public interface LogoutByLoginHistorySchedulerRepository {
    LogoutByLoginHistoryScheduler save(LogoutByLoginHistoryScheduler entity);
    void delete(Long id);
}
