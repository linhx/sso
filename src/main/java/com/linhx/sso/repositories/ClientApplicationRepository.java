package com.linhx.sso.repositories;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.ClientApplication;

import java.util.Optional;

/**
 * ClientApplicationRepository
 *
 * @author linhx
 * @since 08/11/2020
 */
public interface ClientApplicationRepository {
    Optional<ClientApplication> findByHost(String host) throws BaseException;
    Optional<ClientApplication> findByClientIdAndSecret(String clientId, String secret) throws BaseException;
}
