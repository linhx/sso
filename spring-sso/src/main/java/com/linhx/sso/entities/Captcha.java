package com.linhx.sso.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Captcha
 *
 * @author linhx
 * @since 26/11/2021
 */
@Getter
@Setter
public class Captcha extends Base {
    public Captcha(String value) {
        this.value = value;
    }

    @Id
    private String id;

    String value;
}
