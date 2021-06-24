package com.linhx.sso.repositories;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.RequestAccessToken;

import java.util.Optional;

/**
 * RequestAccessTokenRepository
 *
 * @author linhx
 * @since 08/11/2020
 */
public interface RequestAccessTokenRepository {
    RequestAccessToken create(Long userId, Long clientAppId) throws BaseException;

    Optional<RequestAccessToken> findValidByUuid(String uuid) throws BaseException;

    long deleteInvalid();

    RequestAccessToken save(RequestAccessToken requestAccessToken);
}
