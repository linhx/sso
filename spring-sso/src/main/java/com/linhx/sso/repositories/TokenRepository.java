package com.linhx.sso.repositories;

import com.linhx.sso.entities.Token;

import java.util.List;
import java.util.Optional;

/**
 * TokenRepository
 *
 * @author linhx
 * @since 17/08/2021
 */
public interface TokenRepository {
    Token save(Token token);
    List<Long> findInvalidIds();
    List<Long> findValidByUserId(Long userId);
    List<Long> findValidByLoginHistoryId(Long loginHistoryId);
    Optional<Token> findById(Long id);
    void invalidateByUserId(Long userId);
    void invalidateByLoginHistoryId(Long userId);
    List<Token> findExpiredTokens();
    void deleteExpiredTokens();
}
