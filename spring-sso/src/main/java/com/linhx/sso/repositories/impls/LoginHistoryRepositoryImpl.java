package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.LoginHistory;
import com.linhx.sso.repositories.LoginHistoryRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

interface LoginHistoryMongoDbRepository extends MongoRepository<LoginHistory, Long> {
}

/**
 * LoginHistoryRepositoryImpl
 *
 * @author linhx
 * @since 18/08/2021
 */
@Service
public class LoginHistoryRepositoryImpl implements LoginHistoryRepository {
    private final LoginHistoryMongoDbRepository loginHistoryMongoDbRepository;

    public LoginHistoryRepositoryImpl(LoginHistoryMongoDbRepository loginHistoryMongoDbRepository) {
        this.loginHistoryMongoDbRepository = loginHistoryMongoDbRepository;
    }

    public LoginHistory save(LoginHistory loginHistory) {
        return this.loginHistoryMongoDbRepository.save(loginHistory);
    }

    public void deleteAllById(Iterable<? extends Long> ids) {
        this.loginHistoryMongoDbRepository.deleteAllById(ids);
    }
}
