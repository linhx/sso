package com.linhx.sso.repositories.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.RequestAccessToken;
import com.linhx.sso.repositories.RequestAccessTokenRepository;
import com.linhx.sso.repositories.SequenceRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface RequestAccessTokenRepositoryMongoDbRepository extends MongoRepository<RequestAccessToken, Long> {
    @Query(value = "{$and: [{'uuid': ?0}, {'isValid': true}, { 'expired': { $gt: new Date() } }]}")
    Optional<RequestAccessToken> findValidByUuid(String uuid);

    @Query(value = "{$or: [{'isValid': false} , {'expired':{$lte: new Date()}}]}", delete = true)
    long deleteInvalid();
}

/**
 * RequestAccessTokenRepositoryImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Repository
public class RequestAccessTokenRepositoryImpl implements RequestAccessTokenRepository {
    private final RequestAccessTokenRepositoryMongoDbRepository requestAccessTokenRepositoryMongoDb;
    private final SequenceRepository sequenceRepository;

    public RequestAccessTokenRepositoryImpl(RequestAccessTokenRepositoryMongoDbRepository requestAccessTokenRepositoryMongoDb, SequenceRepository sequenceRepository) {
        this.requestAccessTokenRepositoryMongoDb = requestAccessTokenRepositoryMongoDb;
        this.sequenceRepository = sequenceRepository;
    }

    @Override
    public RequestAccessToken create(Long userId, Long clientAppId) throws BaseException {
        var entity = new RequestAccessToken();
        var id = this.sequenceRepository.getNextSequence(RequestAccessToken.SEQ_NAME);
        entity.setId(id);
        entity.setUuid(UUID.randomUUID().toString());
        entity.setUserId(userId);
        entity.setClientApplicationId(clientAppId);
        entity.setValid(true);
        entity.setExpired(Timestamp.from(Instant.now().plusSeconds(SecurityConstants.REQUEST_TOKEN_EXPIRATION_SECONDS)));

        return this.requestAccessTokenRepositoryMongoDb.save(entity);
    }

    @Override
    public Optional<RequestAccessToken> findValidByUuid(String uuid) throws BaseException {
        return this.requestAccessTokenRepositoryMongoDb.findValidByUuid(uuid);
    }

    @Override
    public long deleteInvalid() {
        return this.requestAccessTokenRepositoryMongoDb.deleteInvalid();
    }

    @Override
    public RequestAccessToken save(RequestAccessToken requestAccessToken) {
        return this.requestAccessTokenRepositoryMongoDb.save(requestAccessToken);
    }
}
