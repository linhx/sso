package com.linhx.sso.utils;

import com.github.cage.Cage;

import java.util.Base64;

/**
 * CCage
 *
 * @author linhx
 * @since 19/08/2021
 */
public class CCage extends Cage {
    public CCage() {
        super(null, null, null, null, null, null, null);
    }

    public static final Cage cage = new CCage();

    public static String generateToken() {
        return cage.getTokenGenerator().next();
    }

    public static String drawToBase64(String token) {
        var img = cage.draw(token);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(img);
    }
}
