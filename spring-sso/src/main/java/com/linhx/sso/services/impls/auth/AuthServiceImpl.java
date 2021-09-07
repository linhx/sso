package com.linhx.sso.services.impls.auth;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.BusinessException;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.security.Tokens;
import com.linhx.sso.configs.security.UserDetail;
import com.linhx.sso.constants.Messages;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.response.PrincipalDto;
import com.linhx.sso.entities.LogoutByLoginHistoryScheduler;
import com.linhx.sso.exceptions.GenerateTokenException;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.sso.exceptions.RefreshTokenAlreadyUsedException;
import com.linhx.sso.exceptions.SequenceException;
import com.linhx.sso.repositories.LogoutByLoginHistorySchedulerRepository;
import com.linhx.sso.repositories.SequenceRepository;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.ClientApplicationService;
import com.linhx.sso.services.UserService;
import com.linhx.sso.services.token.TokenService;
import com.linhx.utils.DateTimeUtils;
import com.linhx.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final ClientApplicationService clientApplicationService;
    private final UserService userService;
    private final TokenService tokenService;
    private final LogoutByLoginHistorySchedulerRepository logoutByLoginHistorySchedulerRepo;
    private final SequenceRepository sequenceRepository;
    private final Scheduler scheduler;
    private final EnvironmentVariable env;

    public AuthServiceImpl(ClientApplicationService clientApplicationService, UserService userService,
                           TokenService tokenService,
                           LogoutByLoginHistorySchedulerRepository logoutByLoginHistorySchedulerRepos,
                           SequenceRepository sequenceRepository, Scheduler scheduler, EnvironmentVariable env) {
        this.clientApplicationService = clientApplicationService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.logoutByLoginHistorySchedulerRepo = logoutByLoginHistorySchedulerRepos;
        this.sequenceRepository = sequenceRepository;
        this.scheduler = scheduler;
        this.env = env;
    }

    @Override
    public PrincipalDto auth(AuthDto dto) throws BaseException {
        var clientAppOpt = this.clientApplicationService.findByClientIdAndSecret(dto.getClientId(), dto.getClientSecret());
        if (clientAppOpt.isEmpty()) {
            throw new LoginInfoWrongException("error.auth.clientAppDoesNotExist");
        }
        var clientApp = clientAppOpt.get();

        try {
            var claims = JwtUtils.parse(dto.getAccessToken(), clientApp.getAccessTokenSecret());
            var username = claims.get(SecurityConstants.JWT_USERNAME, String.class);
            return new PrincipalDto(username);
        } catch (Exception e) {
            throw new LoginInfoWrongException("error.auth.invalidAccessToken");
        }
    }

    @Override
    @Transactional
    public Tokens refresh(String refreshToken) throws RefreshTokenAlreadyUsedException {
        try {
            var refreshTokenDetails = this.tokenService.parseRefreshToken(refreshToken);
            synchronized (refreshTokenDetails.getId()) {
                if (this.tokenService.isInvalid(refreshTokenDetails.getId())) {
                    logger.warn("user {}, history {} may be under attack",
                            refreshTokenDetails.getUserId(),
                            refreshTokenDetails.getLh());
                    throw new RefreshTokenAlreadyUsedException(refreshTokenDetails.getLh());
                }

                var userOpt = this.userService.findById(refreshTokenDetails.getUserId());
                if (userOpt.isEmpty()) {
                    throw new LoginInfoWrongException("error.refreshToken.userNotFound");
                }
                var user = userOpt.get();
                if (!user.isActive()) {
                    throw new DisabledException("error.refreshToken.userInactive");
                }
                var userDetail = UserDetail.fromEntity(user);

                JwtUtils.JwtResult refreshTokenResult = this.tokenService.generateRefreshToken(userDetail,
                        refreshTokenDetails.getLh());
                JwtUtils.JwtResult accessTokenResult = this.tokenService.generateAccessToken(userDetail,
                        refreshTokenDetails.getLh(),
                        refreshTokenResult.getTokenId());

                // invalidate the refresh token
                this.tokenService.invalidate(refreshTokenDetails);
                return new Tokens(
                        accessTokenResult.getToken(),
                        accessTokenResult.getExpired(),
                        refreshTokenResult.getToken(),
                        refreshTokenResult.getExpired()
                );
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Request to parse expired JWT: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_REFRESH_TOKEN_INVALID);
        } catch (UnsupportedJwtException e) {
            logger.warn("Request to parse unsupported JWT: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_REFRESH_TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            logger.warn("Request to parse invalid JWT: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_REFRESH_TOKEN_INVALID);
        } catch (SignatureException e) {
            logger.warn("Request to parse WT with invalid signature: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_REFRESH_TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            logger.warn("Request to parse empty or null JWT: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_REFRESH_TOKEN_INVALID);
        } catch (GenerateTokenException e) {
            logger.warn("Can not generate tokens: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_TOKEN_CANT_BE_GENERATED);
        }
    }

    @Override
    public String createLlhsJwt(Long llhsId) throws Exception {
        var jwt = JwtUtils.generate(builder ->
                        builder.claim(SecurityConstants.JWT_LOGOUT_BY_LOGIN_HIS_SCHEDULER_ID, llhsId),
                this.env.getLlhsSecret(),
                AuthService.SCHEDULE_TIME_LOGOUT,
                null);
        return jwt.getToken();
    }

    public Long parseLlhsJwt(String llshJwt) {
        var clams = JwtUtils.parse(llshJwt, this.env.getLlhsSecret());
        return clams.get(SecurityConstants.JWT_LOGOUT_BY_LOGIN_HIS_SCHEDULER_ID, Long.class);
    }

    @Override
    public boolean cancelLogoutByLoginHistoryScheduler(Long logoutByLoginHistorySchedulerId) {
        try {
            var deleted = this.scheduler.deleteJob(JobKey.jobKey(String.valueOf(logoutByLoginHistorySchedulerId),
                    LogoutByLoginHistoryScheduler.class.getName()));
            if (deleted) {
                this.logoutByLoginHistorySchedulerRepo.delete(logoutByLoginHistorySchedulerId);
            }
            return deleted;
        } catch (SchedulerException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            logger.error("error.cancelLogoutByLoginHistoryScheduler.failed", e);
            return false;
        }
    }

    /**
     * @param loginHistoryId
     */
    public LogoutByLoginHistoryScheduler logoutScheduler(Long loginHistoryId) throws BusinessException {
        try {
            var startAt = DateTimeUtils.dateFromNow(SCHEDULE_TIME_LOGOUT);
            var newLogoutScheduler = new LogoutByLoginHistoryScheduler(
                    this.sequenceRepository.getNextSequence(LogoutByLoginHistoryScheduler.SEQ_NAME),
                    loginHistoryId,
                    startAt
            );
            var logoutScheduler = this.logoutByLoginHistorySchedulerRepo.save(newLogoutScheduler);

            var job = JobBuilder.newJob(LogoutByLoginHistoryJob.class)
                    .usingJobData(LogoutByLoginHistoryJob.DATA_KEY_LH_ID, loginHistoryId)
                    .withIdentity(logoutScheduler.getId().toString(), LogoutByLoginHistoryScheduler.class.getName())
                    .build();

            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(logoutScheduler.getId().toString(), LogoutByLoginHistoryScheduler.class.getName())
                    .startAt(startAt)
                    .build();

            this.scheduler.scheduleJob(job, trigger);
            return newLogoutScheduler;
        } catch (SchedulerException | SequenceException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BusinessException("error.refreshToken.cantCreateScheduler");
        }
    }
}
