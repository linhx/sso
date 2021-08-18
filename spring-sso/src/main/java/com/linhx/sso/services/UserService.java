package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.LoginHistory;
import com.linhx.sso.entities.User;

import java.util.Optional;

/**
 * IUserService
 *
 * @author linhx
 * @since 28/10/2020
 */
public interface UserService {
    Optional<User> findByUsername(String username) throws BaseException;
    Optional<User> findById(Long id) throws BaseException;
    Optional<User> findByUuid(String uuid) throws BaseException;
    LoginHistory createLoginHistory(Long userId) throws BaseException;
}
