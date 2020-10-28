package com.linhx.sso.services.impls;

import com.linhx.sso.entities.User;
import com.linhx.sso.services.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserService
 *
 * @author linhx
 * @since 28/10/2020
 */
@Service
public class UserService implements IUserService {
    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }
}
