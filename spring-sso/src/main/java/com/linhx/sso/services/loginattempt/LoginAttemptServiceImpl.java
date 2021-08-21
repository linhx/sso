package com.linhx.sso.services.loginattempt;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.entities.LoginAttempt;
import com.linhx.sso.repositories.LoginAttemptRepository;
import com.linhx.sso.repositories.SequenceRepository;
import com.linhx.utils.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * LoginAttemptServiceImpl
 *
 * @author linhx
 * @since 21/08/2021
 */
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final LoginAttemptRepository loginAttemptRepo;
    private final SequenceRepository sequenceRepo;
    private final EnvironmentVariable env;

    public LoginAttemptServiceImpl(LoginAttemptRepository loginAttemptRepo,
                                   SequenceRepository sequenceRepo,
                                   EnvironmentVariable env) {
        this.loginAttemptRepo = loginAttemptRepo;
        this.sequenceRepo = sequenceRepo;
        this.env = env;
    }

    @Override
    public Optional<LoginAttempt> findByIp(String ip) {
        return this.loginAttemptRepo.findByIp(ip);
    }

    @Override
    public boolean check(String ip) {
        var attemptOpt = this.findByIp(ip);
        if (attemptOpt.isEmpty()) {
            return true;
        }

        var attempt = attemptOpt.get();
        if (attempt.getAt().before(DateTimeUtils.dateFromNow(-this.env.getSecurityLoginAttemptsTimeBlocking()))) {
            this.loginAttemptRepo.delete(attempt);
            return true;
        } else {
            return attempt.getTimes() <= this.env.getSecurityLoginAttemptsFailedAllowed();
        }
    }

    @Override
    public LoginAttempt attemptFailed(String ip) throws BaseException {
        var attemptOpt = this.loginAttemptRepo.findByIp(ip);
        LoginAttempt attempt;
        if (attemptOpt.isPresent()) {
            attempt = attemptOpt.get();
            attempt.setTimes(attempt.getTimes() + 1);
            attempt.setAt(new Date());
        } else {
            attempt = new LoginAttempt(
                    this.sequenceRepo.getNextSequence(LoginAttempt.SEQ_NAME),
                    ip,
                    1,
                    new Date()
            );
        }
        return this.loginAttemptRepo.save(attempt);
    }
}
