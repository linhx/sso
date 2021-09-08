package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.LoginAttempt;
import com.linhx.sso.repositories.LoginAttemptRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface LoginAttemptMongoDbRepository extends MongoRepository<LoginAttempt, Long> {
    @Query(value = "{ ip: ?0 }")
    Optional<LoginAttempt> findByIp(String ip);
}

/**
 * LoginAttemptRepositoryImpl
 *
 * @author linhx
 * @since 21/08/2021
 */
@Repository
public class LoginAttemptRepositoryImpl implements LoginAttemptRepository {
    private final LoginAttemptMongoDbRepository loginAttemptMongoDbRepo;

    public LoginAttemptRepositoryImpl(LoginAttemptMongoDbRepository loginAttemptMongoDbRepo) {
        this.loginAttemptMongoDbRepo = loginAttemptMongoDbRepo;
    }

    @Override
    public LoginAttempt save(LoginAttempt loginAttempt) {
        return this.loginAttemptMongoDbRepo.save(loginAttempt);
    }

    @Override
    public void delete(LoginAttempt loginAttempt) {
        this.loginAttemptMongoDbRepo.delete(loginAttempt);
    }

    @Override
    public Optional<LoginAttempt> findByIp(String ip) {
        return this.loginAttemptMongoDbRepo.findByIp(ip);
    }
}
