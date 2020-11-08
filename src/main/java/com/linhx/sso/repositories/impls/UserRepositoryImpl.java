package com.linhx.sso.repositories.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.NotImplemented;
import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepositoryImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findByUsername(String username) throws BaseException {
        throw new NotImplemented();
    }

    @Override
    public Optional<User> findById(Long id) throws BaseException {
        throw new NotImplemented();
    }
}
