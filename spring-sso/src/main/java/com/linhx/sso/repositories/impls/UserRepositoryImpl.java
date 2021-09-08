package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.User;
import com.linhx.sso.repositories.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface UserRepositoryMongoDbRepository extends MongoRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    Optional<User> findByUuid(String uuid);

    /**
     * find active by username or email
     *
     * @param identifier username or email
     * @return
     */
    @Query(value = "{$and: [{isActive: true}, {$or: [{username: ?0} , {email: ?0}]}]}")
    Optional<User> findByIdentifier(String identifier);
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

    @Override
    public Optional<User> findByIdentifier(String identifier) {
        return this.userRepositoryMongoDb.findByIdentifier(identifier);
    }

    @Override
    public User save(User entity) {
        return this.userRepositoryMongoDb.save(entity);
    }
}
