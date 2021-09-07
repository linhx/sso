package com.linhx.sso.services.token;

import com.linhx.sso.configs.security.UserDetail;
import com.linhx.sso.entities.Token;
import com.linhx.sso.exceptions.GenerateTokenException;
import com.linhx.sso.exceptions.ParseTokenException;
import com.linhx.utils.JwtUtils;
import com.linhx.utils.functions.F;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author linhx
 * @since 08/10/2020
 */
public interface TokenService {
    JwtUtils.JwtResult generateToken(Long userId, Long loginHistoryId, F<Token, JwtUtils.JwtResult> f) throws GenerateTokenException;

    JwtUtils.JwtResult generateAccessToken(UserDetail user, Long loginHistoryId, Long refreshTokenId) throws GenerateTokenException;

    JwtUtils.JwtResult generateRefreshToken(UserDetail user, Long loginHistoryId) throws GenerateTokenException;

    TokenDetail parseAccessToken(String token) throws ParseTokenException;

    RefreshTokenDetail parseRefreshToken(String token);

    void invalidate(RefreshTokenDetail tokenDetails);

    void invalidate(TokenDetail tokenDetail);

    void invalidateByUserId(Long userId);

    void invalidateByLoginHistoryId(Long loginHistoryId);

    boolean isInvalid(Long id);

    void deleteExpiredTokens();

    /**
     * Gets the authorities.
     *
     * @return the authorities
     */
    Collection<? extends GrantedAuthority> getAuthorities(UserDetail user);
}
