package com.linhx.sso.repositories.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.entities.ClientApplication;
import com.linhx.sso.repositories.ClientApplicationRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface ClientApplicationMongoDbRepository extends MongoRepository<ClientApplication, Long> {
    Optional<ClientApplication> findByHost(String host);

    Optional<ClientApplication> findByClientIdAndSecret(String clientId, String secret);
}

/**
 * ClientApplicationRepositoryImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Repository
public class ClientApplicationRepositoryImpl implements ClientApplicationRepository {
    private final ClientApplicationMongoDbRepository clientApplicationMongoDb;

    public ClientApplicationRepositoryImpl(ClientApplicationMongoDbRepository clientApplicationMongoDb) {
        this.clientApplicationMongoDb = clientApplicationMongoDb;
    }

    @Override
    public Optional<ClientApplication> findByHost(String host) throws BaseException {
        return this.clientApplicationMongoDb.findByHost(host);
    }

    @Override
    public Optional<ClientApplication> findByClientIdAndSecret(String clientId, String secret) throws BaseException {
        return this.clientApplicationMongoDb.findByClientIdAndSecret(clientId, secret);
    }
}