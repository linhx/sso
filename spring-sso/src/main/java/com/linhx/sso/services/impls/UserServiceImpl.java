package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.BusinessException;
import com.linhx.exceptions.ResetPasswordException;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.MailConfig;
import com.linhx.sso.controller.dtos.request.ResetPasswordDto;
import com.linhx.sso.controller.dtos.request.ResetPasswordRequestDto;
import com.linhx.sso.entities.LoginHistory;
import com.linhx.sso.entities.ResetPasswordRequest;
import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.LoginHistoryRepository;
import com.linhx.sso.repositories.SequenceRepository;
import com.linhx.sso.repositories.UserRepository;
import com.linhx.sso.services.MailService;
import com.linhx.sso.services.ResetPasswordRequestService;
import com.linhx.sso.services.UserService;
import org.bson.BsonTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    private final MailService mailService;
    private final UserRepository userRepository;
    private final ResetPasswordRequestService resetPasswordRequestService;
    private final SequenceRepository sequenceRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailConfig mailConfig;
    private final EnvironmentVariable env;

    public UserServiceImpl(MailService mailService, UserRepository userRepository,
                           ResetPasswordRequestService resetPasswordRequestService,
                           SequenceRepository sequenceRepository,
                           LoginHistoryRepository loginHistoryRepository,
                           PasswordEncoder passwordEncoder,
                           MailConfig mailConfig, EnvironmentVariable env) {
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.resetPasswordRequestService = resetPasswordRequestService;
        this.sequenceRepository = sequenceRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailConfig = mailConfig;
        this.env = env;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return this.userRepository.findByUuid(uuid);
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public LoginHistory createLoginHistory(Long userId) throws BaseException {
        var loginHistory = new LoginHistory(
                this.sequenceRepository.getNextSequence(LoginHistory.SEQ_NAME),
                userId
        );
        return this.loginHistoryRepository.save(loginHistory);
    }

    @Override
    public void changePassword(User user, String newPassword) {
        String passwordHash = this.passwordEncoder.encode(newPassword);
        user.setPassword(passwordHash);
        user.setUpdatedAt(new Date());
        this.save(user);
    }

    @Override
    public void requestResetPassword(ResetPasswordRequestDto resetPasswordRequestDto) throws BusinessException {
        Optional<User> userOpt = this.userRepository.findByIdentifier(resetPasswordRequestDto.getIdentifier());
        if (userOpt.isEmpty()) {
            throw new BusinessException("error.resetPassword.userNotFound");
        }

        User user = userOpt.get();

        // create request
        ResetPasswordRequest resetPasswordRequest = this.resetPasswordRequestService.create(user);
        this.mailService.adminSend(
                user.getEmail(),
                this.mailConfig.getResetPasswordSubject(),
                this.mailConfig.getResetPasswordContent(
                        user.getUsername(),
                        this.env.getResetPasswordUrl(resetPasswordRequest.getToken())));
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, String token) throws ResetPasswordException {
        Optional<ResetPasswordRequest> resetPasswordRequestOpt
                = this.resetPasswordRequestService.findByToken(token);
        if (resetPasswordRequestOpt.isEmpty() ||
                !resetPasswordRequestOpt.get().isActive() ||
                resetPasswordRequestOpt.get().getExpired().compareTo(new BsonTimestamp(System.currentTimeMillis())) <= 0) {
            throw new ResetPasswordException("error.resetPassword.requestExpired");
        }

        try {
            ResetPasswordRequest resetPasswordRequest = resetPasswordRequestOpt.get();

            // change password
            Optional<User> userOpt = this.userRepository.findById(resetPasswordRequestOpt.get().getUserId());
            if (userOpt.isPresent() && userOpt.get().isActive()) {
                this.changePassword(userOpt.get(), resetPasswordDto.getPassword());
            }

            // delete the request
            this.resetPasswordRequestService.delete(resetPasswordRequest);
        } catch (Exception e) {
            throw new ResetPasswordException("error.resetPassword.unknown");
        }
    }
}
