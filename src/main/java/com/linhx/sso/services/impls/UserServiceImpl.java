package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.NotImplemented;
import com.linhx.sso.entities.User;
import com.linhx.sso.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserService
 *
 * @author linhx
 * @since 28/10/2020
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public Optional<User> findByUsername(String username) throws BaseException {
        throw new NotImplemented();
    }

    @Override
    public Optional<User> findById(Long id) throws BaseException {
        throw new NotImplemented();
    }
}
