package com.linhx.utils;

import java.util.Base64;
import java.util.UUID;

/**
 * HashingUtils
 *
 * @author linhx
 * @since 08/09/2021
 */
public class HashingUtils {
    private HashingUtils() {
    }

    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    public static String toBase64(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String randomBase64() {
        return toBase64(UUID());
    }

    public static String randomBase64Url() {
        return randomBase64()
                .replace('+', '-')
                .replace('/', '_')
                .replace("=", "");
    }
}
