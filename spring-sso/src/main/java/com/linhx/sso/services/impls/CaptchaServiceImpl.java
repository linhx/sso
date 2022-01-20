package com.linhx.sso.services.impls;

import com.linhx.sso.controller.dtos.request.CaptchaReqDto;
import com.linhx.sso.controller.dtos.response.CaptchaDto;
import com.linhx.sso.repositories.CaptchaRepository;
import com.linhx.sso.services.CaptchaService;
import com.linhx.sso.utils.CCage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * CaptchaServiceImpl
 *
 * @author linhx
 * @since 26/11/2021
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    private final CaptchaRepository captchaRepo;

    public CaptchaServiceImpl(CaptchaRepository captchaRepo) {
        this.captchaRepo = captchaRepo;
    }

    @Override
    public CaptchaDto create() {
        var value = CCage.generateToken();
        var captcha = this.captchaRepo.create(value);
        var image = CCage.drawToBase64(value);
        return new CaptchaDto(captcha.getId(), image);
    }

    @Override
    public boolean isValid(CaptchaReqDto captcha) {
        var captchaOpt = this.captchaRepo.findById(captcha.getId());
        if (captchaOpt.isPresent()) {
            this.captchaRepo.deleteById(captcha.getId());
            return captchaOpt.get().getValue().equals(captcha.getValue());
        }
        return false;
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void deleteOld() {
        var date = new Date(System.currentTimeMillis() - 86400000);
        this.captchaRepo.deleteCreatedAtLte(date);
    }
}
