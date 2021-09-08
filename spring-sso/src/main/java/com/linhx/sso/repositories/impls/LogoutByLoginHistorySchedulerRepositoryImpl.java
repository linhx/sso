package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.LogoutByLoginHistoryScheduler;
import com.linhx.sso.repositories.LogoutByLoginHistorySchedulerRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

interface LogoutByLoginHistorySchedulerMongoDbRepository extends MongoRepository<LogoutByLoginHistoryScheduler, Long> {

}

/**
 * LogoutByLoginHistorySchedulerRepositoryImpl
 *
 * @author linhx
 * @since 07/09/2021
 */
@Repository
public class LogoutByLoginHistorySchedulerRepositoryImpl implements LogoutByLoginHistorySchedulerRepository {
    private final LogoutByLoginHistorySchedulerMongoDbRepository logoutByLoginHistorySchedulerMongoDbRepo;

    public LogoutByLoginHistorySchedulerRepositoryImpl(LogoutByLoginHistorySchedulerMongoDbRepository
                                                               logoutByLoginHistorySchedulerMongoDbRepo) {
        this.logoutByLoginHistorySchedulerMongoDbRepo = logoutByLoginHistorySchedulerMongoDbRepo;
    }

    @Override
    public LogoutByLoginHistoryScheduler save(LogoutByLoginHistoryScheduler entity) {
        return this.logoutByLoginHistorySchedulerMongoDbRepo.save(entity);
    }

    @Override
    public void delete(Long id) {
        this.logoutByLoginHistorySchedulerMongoDbRepo.deleteById(id);
    }
}