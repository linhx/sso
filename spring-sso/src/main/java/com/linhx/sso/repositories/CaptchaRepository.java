package com.linhx.sso.repositories;

import com.linhx.sso.entities.Captcha;

import java.util.Date;
import java.util.Optional;

/**
 * CaptchaRepository
 *
 * @author linhx
 * @since 26/11/2021
 */
public interface CaptchaRepository {
    Captcha create(String value);
    Optional<Captcha> findById(String id);
    void deleteById(String id);
    void deleteCreatedAtLte(Date date);
}
