package com.linhx.sso.repositories;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.User;

import java.util.Optional;

/**
 * UserRepository
 *
 * @author linhx
 * @since 08/11/2020
 */
public interface UserRepository {
    Optional<User> findByUsername(String username) throws BaseException;

    Optional<User> findById(Long id) throws BaseException;

    Optional<User> findByUuid(String uuid) throws BaseException;
}
