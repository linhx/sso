package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.configs.security.Tokens;
import com.linhx.sso.configs.security.UserDetail;
import com.linhx.sso.constants.Messages;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.response.PrincipalDto;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.ClientApplicationService;
import com.linhx.sso.services.UserService;
import com.linhx.sso.services.token.TokenService;
import com.linhx.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

    public AuthServiceImpl(ClientApplicationService clientApplicationService, UserService userService, TokenService tokenService) {
        this.clientApplicationService = clientApplicationService;
        this.userService = userService;
        this.tokenService = tokenService;
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
    public Tokens refresh(String refreshToken) {
        try {
            var refreshTokenDetails = this.tokenService.parseRefreshToken(refreshToken);
            synchronized (refreshTokenDetails.getId()) {
                if (this.tokenService.isInvalid(refreshTokenDetails.getId())) {
                    this.tokenService.invalidateByLoginHistoryId(refreshTokenDetails.getLh());
                    logger.warn("user {}, history {} may be under attack", refreshTokenDetails.getUserId(), refreshTokenDetails.getLh());
                    throw new LoginInfoWrongException("error.refreshToken.alreadyUsed");
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
        } catch (Exception e) {
            logger.warn("Exception JWT: {}", e.getMessage());
            throw new LoginInfoWrongException(Messages.ERR_REFRESH_TOKEN_INVALID);
        }
    }
}
