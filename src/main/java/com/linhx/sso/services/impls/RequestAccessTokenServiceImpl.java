package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.BusinessException;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.security.Tokens;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.GrantAccessTokenDto;
import com.linhx.sso.entities.RequestAccessToken;
import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.ClientApplicationRepository;
import com.linhx.sso.repositories.RequestAccessTokenRepository;
import com.linhx.sso.services.RequestAccessTokenService;
import com.linhx.sso.services.UserService;
import com.linhx.utils.JwtUtils;
import com.linhx.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.Optional;

/**
 * ClientApplicationServiceImpl
 *
 * @author linhx
 * @since 07/11/2020
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class RequestAccessTokenServiceImpl implements RequestAccessTokenService {
    private final EnvironmentVariable env;
    private final RequestAccessTokenRepository requestAccessTokenRepository;
    private final ClientApplicationRepository clientApplicationRepository;
    private final UserService userService;

    public RequestAccessTokenServiceImpl(EnvironmentVariable env,
                                         RequestAccessTokenRepository requestAccessTokenRepository,
                                         ClientApplicationRepository clientApplicationRepository,
                                         UserService userService) {
        this.env = env;
        this.requestAccessTokenRepository = requestAccessTokenRepository;
        this.clientApplicationRepository = clientApplicationRepository;
        this.userService = userService;
    }

    @Override
    public String createRequestAccessTokenUrl(User user, String callbackUrlStr) throws Exception {
        var callbackUrl = new URL(callbackUrlStr);
        var clientApplicationOpt =
                this.clientApplicationRepository.findByHost(callbackUrl.getHost());
        if (clientApplicationOpt.isEmpty()) {
            throw new BusinessException("error.createRat.clientAppNotFound");
        }
        var clientApplication = clientApplicationOpt.get();
        var userOpt = this.userService.findByUuid(user.getUuid());
        if (userOpt.isEmpty()) {
            throw new BusinessException("error.createRat.userNotFound");
        }
        var request = this.requestAccessTokenRepository.create(userOpt.get().getId(),
                clientApplication.getId());
        var jwtResult = JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_RAT_UUID,
                request.getUuid()),
                this.env.getRequestAccessTokenSecret(),
                SecurityConstants.REQUEST_TOKEN_EXPIRATION_SECONDS);

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(clientApplication.getSignInUrl())
                .queryParam(SecurityConstants.REQUEST_ACCESS_TOKEN, jwtResult.getToken())
                .queryParam(SecurityConstants.CALLBACK, callbackUrlStr)
                .build();
        return uriComponents.toUriString();
    }

    @Override
    public Optional<RequestAccessToken> findValidByUuid(String uuid) throws BaseException {
        return this.requestAccessTokenRepository.findValidByUuid(uuid);
    }

    @Override
    public Object grantAccessToken(GrantAccessTokenDto dto) throws BaseException {
        var clientAppOpt =
                this.clientApplicationRepository.findByClientIdAndSecret(dto.getClientId(), dto.getClientSecret());
        if (clientAppOpt.isEmpty()) {
            throw new BusinessException("error.grantAccessToken.clientAppNotFound");
        }
        var clientApp = clientAppOpt.get();

        String ratUuid;
        try {
            var ratClaims = JwtUtils.parse(dto.getRat(), this.env.getRequestAccessTokenSecret());
            ratUuid = ratClaims.get(SecurityConstants.JWT_RAT_UUID, String.class);
        } catch (Exception e) {
            throw new BusinessException("error.grantAccessToken.invalidRat");
        }
        if (StringUtils.isEmpty(ratUuid)) {
            throw new BusinessException("error.grantAccessToken.invalidRat");
        }
        var ratOpt = this.findValidByUuid(ratUuid);
        if (ratOpt.isEmpty()) {
            throw new BusinessException("error.grantAccessToken.ratDoesNotExist");
        }
        var rat = ratOpt.get();
        if (!rat.getClientApplicationId().equals(clientApp.getId())) {
            throw new BusinessException("error.grantAccessToken.ratDoesNotMatchClientApp");
        }
        var userOpt = this.userService.findById(rat.getUserId());
        if (userOpt.isEmpty()) {
            throw new BusinessException("error.grantAccessToken.userNotFound");
        }
        var user = userOpt.get();

        try {
            var accessTokenJwtResult = JwtUtils.generate(builder ->
                            builder.claim(SecurityConstants.JWT_USERNAME, user.getUsername()),
                    clientApp.getAccessTokenSecret(),
                    SecurityConstants.TOKEN_EXPIRATION_SECONDS);

            var refreshTokenJwtResult = JwtUtils.generate(builder ->
                            builder.claim(SecurityConstants.JWT_USERNAME, user.getUsername()),
                    clientApp.getRefreshTokenSecret(),
                    SecurityConstants.TOKEN_EXPIRATION_SECONDS);

            return new Tokens(accessTokenJwtResult.getToken(), accessTokenJwtResult.getExpired(),
                    refreshTokenJwtResult.getToken(), refreshTokenJwtResult.getExpired());

            // TODO return callback
        } catch (Exception e) {
            throw new BusinessException("error.grantAccessToken.cantCreateJwt");
        } finally {
            rat.setValid(false);
            this.requestAccessTokenRepository.save(rat);
        }
    }

    @Override
    public void deleteInvalidRequestsAccessToken() throws BaseException {
        this.requestAccessTokenRepository.deleteInvalid();
    }
}
