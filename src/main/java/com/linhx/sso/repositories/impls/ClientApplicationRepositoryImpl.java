package com.linhx.sso.repositories.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.NotImplemented;
import com.linhx.sso.entities.ClientApplication;
import com.linhx.sso.repositories.ClientApplicationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ClientApplicationRepositoryImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Repository
public class ClientApplicationRepositoryImpl implements ClientApplicationRepository {
    @Override
    public Optional<ClientApplication> findByHost(String host) throws BaseException {
        throw new NotImplemented();
    }

    @Override
    public Optional<ClientApplication> findByClientIdAndSecret(String clientId, String secret) throws BaseException {
        throw new NotImplemented();
    }
}
