package com.linhx.sso.repositories.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.NotImplemented;
import com.linhx.sso.entities.RequestAccessToken;
import com.linhx.sso.repositories.RequestAccessTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RequestAccessTokenRepositoryImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Repository
public class RequestAccessTokenRepositoryImpl implements RequestAccessTokenRepository {
    @Override
    public RequestAccessToken create(Long userId, Long clientAppId) throws BaseException {
        throw new NotImplemented();
    }

    @Override
    public Optional<RequestAccessToken> findValidByUuid(String uuid) throws BaseException {
        throw new NotImplemented();
    }
}
