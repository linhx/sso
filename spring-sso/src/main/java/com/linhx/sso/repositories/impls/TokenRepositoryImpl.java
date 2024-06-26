package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.Token;
import com.linhx.sso.repositories.TokenRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

interface TokenRepositoryMongoDb extends MongoRepository<Token, Long> {
    @Query(value = "{ isInvalid: true, expired: { $gte: new Date() } }", fields = "{ 'id' : 1 }")
    List<Token> findInvalidTokens();

    @Query(value = "{ isInvalid: false, userId: ?0 }", fields = "{ 'id' : 1 }")
    List<Token> findValidByUserId(Long userId);

    @Query(value = "{ isInvalid: false, loginHistoryId: ?0 }", fields = "{ 'id' : 1 }")
    List<Token> findValidByLoginHistoryId(Long loginHistoryId);

    @Query(value = "{ expired: { $lt: new Date() } }", fields = "{ 'id' : 1 }")
    List<Token> findExpiredTokens();

    @Query(value = "{'expired':{$lt: new Date()}}", delete = true)
    long deleteInvalid();
}

/**
 * TokenRepositoryImpl
 *
 * @author linhx
 * @since 17/08/2021
 */
@Repository
public class TokenRepositoryImpl implements TokenRepository {
    private final TokenRepositoryMongoDb tokenRepositoryMongoDb;
    private final MongoOperations mongoOperations;

    public TokenRepositoryImpl(TokenRepositoryMongoDb tokenRepositoryMongoDb, MongoOperations mongoOperations) {
        this.tokenRepositoryMongoDb = tokenRepositoryMongoDb;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Token save(Token token) {
        return this.tokenRepositoryMongoDb.save(token);
    }

    @Override
    public List<Long> findInvalidIds() {
        return this.tokenRepositoryMongoDb.findInvalidTokens().stream().map(Token::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> findValidByUserId(Long userId) {
        return this.tokenRepositoryMongoDb.findValidByUserId(userId).stream()
                .map(Token::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> findValidByLoginHistoryId(Long loginHistoryId) {
        return this.tokenRepositoryMongoDb.findValidByLoginHistoryId(loginHistoryId).stream()
                .map(Token::getId).collect(Collectors.toList());
    }

    @Override
    public Optional<Token> findById(Long id) {
        return this.tokenRepositoryMongoDb.findById(id);
    }

    @Override
    public void invalidateByUserId(Long userId) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        Update update = new Update();
        update.set("isInvalid", true);
        this.mongoOperations.updateMulti(query, update, Token.class);
    }

    @Override
    public void invalidateByLoginHistoryId(Long loginHistoryId) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("loginHistoryId").is(loginHistoryId));
        Update update = new Update();
        update.set("isInvalid", true);
        this.mongoOperations.updateMulti(query, update, Token.class);
    }

    @Override
    public List<Token> findExpiredTokens() {
        return this.tokenRepositoryMongoDb.findExpiredTokens();
    }

    @Override
    public void deleteExpiredTokens() {
        this.tokenRepositoryMongoDb.deleteInvalid();
    }
}
