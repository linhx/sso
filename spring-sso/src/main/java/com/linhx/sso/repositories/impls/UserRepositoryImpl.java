package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface UserRepositoryMongoDbRepository extends MongoRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    Optional<User> findByUuid(String uuid);
}

/**
 * UserRepositoryImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserRepositoryMongoDbRepository userRepositoryMongoDb;

    public UserRepositoryImpl(UserRepositoryMongoDbRepository userRepositoryMongoDb) {
        this.userRepositoryMongoDb = userRepositoryMongoDb;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepositoryMongoDb.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.userRepositoryMongoDb.findById(id);
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return this.userRepositoryMongoDb.findByUuid(uuid);
    }
}
