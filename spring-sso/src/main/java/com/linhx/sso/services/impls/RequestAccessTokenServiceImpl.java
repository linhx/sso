package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.NotImplemented;
import com.linhx.sso.controller.dtos.request.GrantAccessTokenDto;
import com.linhx.sso.entities.RequestAccessToken;
import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.RequestAccessTokenRepository;
import com.linhx.sso.services.RequestAccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RequestAccessTokenRepository requestAccessTokenRepository;

    public RequestAccessTokenServiceImpl(RequestAccessTokenRepository requestAccessTokenRepository) {
        this.requestAccessTokenRepository = requestAccessTokenRepository;
    }

    @Override
    public String createRequestAccessTokenUrl(User user, String callbackUrlStr) throws Exception {
        throw new NotImplemented();
    }

    @Override
    public Optional<RequestAccessToken> findValidByUuid(String uuid) throws BaseException {
        return this.requestAccessTokenRepository.findValidByUuid(uuid);
    }

    @Override
    public Object grantAccessToken(GrantAccessTokenDto dto) throws BaseException {
        throw new NotImplemented();
    }

    @Override
    public void deleteInvalidRequestsAccessToken() {
        this.requestAccessTokenRepository.deleteInvalid();
    }
}
