package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.Captcha;
import com.linhx.sso.repositories.CaptchaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;


interface CaptchaMongoDbRepository extends MongoRepository<Captcha, String> {
    @Query(value = "{'createdAt':{$lte: ?0}}", delete = true)
    void deleteCreatedAtLte(Date date);
}

/**
 * CaptchaRepositoryImpl
 *
 * @author linhx
 * @since 26/11/2021
 */
@Repository
public class CaptchaRepositoryImpl implements CaptchaRepository {
    private final CaptchaMongoDbRepository captchaMongoDbRepository;

    public CaptchaRepositoryImpl(CaptchaMongoDbRepository captchaMongoDbRepository) {
        this.captchaMongoDbRepository = captchaMongoDbRepository;
    }

    @Override
    public Captcha create(String value) {
        return this.captchaMongoDbRepository.save(new Captcha(value));
    }

    @Override
    public Optional<Captcha> findById(String id) {
        return this.captchaMongoDbRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        this.captchaMongoDbRepository.deleteById(id);
    }

    @Override
    public void deleteCreatedAtLte(Date date) {
        this.captchaMongoDbRepository.deleteCreatedAtLte(date);
    }
}
