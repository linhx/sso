package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.ResetPasswordRequest;
import com.linhx.sso.exceptions.SequenceException;
import com.linhx.sso.repositories.ResetPasswordRequestRepository;
import com.linhx.sso.repositories.SequenceRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

interface ResetPasswordRequestMongoDbRepository extends MongoRepository<ResetPasswordRequest, Long> {
    List<ResetPasswordRequest> findAllByUserId(Long userId);

    Optional<ResetPasswordRequest> findByToken(String token);
}

/**
 * ResetPasswordRequestRepositoryImpl
 *
 * @author linhx
 * @since 08/09/2021
 */
@Repository
public class ResetPasswordRequestRepositoryImpl implements ResetPasswordRequestRepository {
    private final ResetPasswordRequestMongoDbRepository db;
    private final SequenceRepository sequenceRepository;

    public ResetPasswordRequestRepositoryImpl(ResetPasswordRequestMongoDbRepository db,
                                              SequenceRepository sequenceRepository) {
        this.db = db;
        this.sequenceRepository = sequenceRepository;
    }

    @Override
    public ResetPasswordRequest insert(ResetPasswordRequest entity) throws SequenceException {
        entity.setId(this.sequenceRepository.getNextSequence(ResetPasswordRequest.SEQ_NAME));
        return this.db.insert(entity);
    }

    @Override
    public ResetPasswordRequest save(ResetPasswordRequest entity) {
        return this.db.save(entity);
    }

    @Override
    public Optional<ResetPasswordRequest> findByToken(String token) {
        return this.db.findByToken(token);
    }

    @Override
    public List<ResetPasswordRequest> findByUserId(Long userId) {
        return this.db.findAllByUserId(userId);
    }

    @Override
    public ResetPasswordRequest delete(ResetPasswordRequest entity) {
        this.db.delete(entity);
        return entity;
    }
}
