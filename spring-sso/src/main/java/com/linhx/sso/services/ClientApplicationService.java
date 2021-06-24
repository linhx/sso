package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.ClientApplication;

import java.util.Optional;

/**
 * ClientApplicationService
 *
 * @author linhx
 * @since 08/11/2020
 */
public interface ClientApplicationService {
    Optional<ClientApplication> findByClientIdAndSecret(String clientId, String secret) throws BaseException;
}
