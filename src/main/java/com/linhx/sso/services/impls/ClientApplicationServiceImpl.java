package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.ClientApplication;
import com.linhx.sso.repositories.ClientApplicationRepository;
import com.linhx.sso.services.ClientApplicationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ClientApplicationServiceImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Service
public class ClientApplicationServiceImpl implements ClientApplicationService {
    private final ClientApplicationRepository clientApplicationRepository;

    public ClientApplicationServiceImpl(ClientApplicationRepository clientApplicationRepository) {
        this.clientApplicationRepository = clientApplicationRepository;
    }

    @Override
    public Optional<ClientApplication> findByClientIdAndSecret(String clientId, String secret) throws BaseException {
        return this.clientApplicationRepository.findByClientIdAndSecret(clientId, secret);
    }
}
