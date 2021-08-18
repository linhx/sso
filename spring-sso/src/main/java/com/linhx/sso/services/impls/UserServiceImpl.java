package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.LoginHistory;
import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.LoginHistoryRepository;
import com.linhx.sso.repositories.SequenceRepository;
import com.linhx.sso.repositories.UserRepository;
import com.linhx.sso.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UserService
 *
 * @author linhx
 * @since 28/10/2020
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SequenceRepository sequenceRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public UserServiceImpl(UserRepository userRepository, SequenceRepository sequenceRepository,
                           LoginHistoryRepository loginHistoryRepository) {
        this.userRepository = userRepository;
        this.sequenceRepository = sequenceRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) throws BaseException {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) throws BaseException {
        return this.userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUuid(String uuid) throws BaseException {
        return this.userRepository.findByUuid(uuid);
    }

    @Override
    public LoginHistory createLoginHistory(Long userId) throws BaseException {
        var loginHistory = new LoginHistory(
                this.sequenceRepository.getNextSequence(LoginHistory.SEQ_NAME),
                userId
        );
        return this.loginHistoryRepository.save(loginHistory);
    }
}
