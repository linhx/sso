package com.linhx.sso.services;

import com.linhx.sso.entities.User;

import java.util.Optional;

/**
 * IUserService
 *
 * @author linhx
 * @since 28/10/2020
 */
public interface IUserService {
    Optional<User> findByUsername(String username);
}
