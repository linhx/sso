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
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    Optional<User> findByUuid(String uuid);

    /**
     *
     * @param identifier username or email
     * @return
     */
    Optional<User> findByIdentifier(String identifier);

    User save(User entity);
}
