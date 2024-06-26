package com.linhx.utils;

import com.linhx.utils.functions.C;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JwtUtils
 *
 * @author linhx
 * @since 09/10/2020
 */
public class JwtUtils {
    @AllArgsConstructor
    @Getter
    public static class JwtResult {
        private Long tokenId;
        private String token;
        private Date expired;
    }
    private static final Logger logger = Logger.getLogger(JwtUtils.class.getName()); // TODO log4j

    private static SecretKey getSecretKey (String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static JwtResult generate(C<JwtBuilder> c, SecretKey secret, long expiredTime, Long tokenId) throws Exception {
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT");
        c.accept(builder);
        Date expired = Date.from(LocalDateTime.now().plusSeconds(expiredTime).atZone(ZoneId.systemDefault()).toInstant());
        String token = builder.setExpiration(expired)
                .signWith(secret)
                .compact();
        return new JwtResult(tokenId, token, expired);
    }

    public static JwtResult generate(C<JwtBuilder> c, String secret, long expiredTime, Long tokenId) throws Exception {
        return generate(c, getSecretKey(secret), expiredTime, tokenId);
    }

    public static Claims parse (String jwt, SecretKey secret) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(secret).build()
                .parseClaimsJws(jwt);
        return jws.getBody();
    }

    public static Claims parse(String jwt, String secret) {
        return parse(jwt, getSecretKey(secret));
    }
}
