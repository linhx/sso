package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.controller.dtos.request.GrantAccessTokenDto;
import com.linhx.sso.entities.RequestAccessToken;
import com.linhx.sso.entities.User;

import java.util.Optional;

/**
 * ClientApplicationService
 *
 * @author linhx
 * @since 07/11/2020
 */
public interface RequestAccessTokenService {
    String createRequestAccessTokenUrl(User user, String callback) throws Exception;
    Optional<RequestAccessToken> findValidByUuid(String uuid) throws BaseException;
    Object grantAccessToken(GrantAccessTokenDto dto) throws BaseException;
    void deleteInvalidRequestsAccessToken() throws BaseException;
}
