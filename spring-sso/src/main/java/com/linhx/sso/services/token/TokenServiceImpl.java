package com.linhx.sso.services.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.security.UserDetail;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.Token;
import com.linhx.sso.exceptions.GenerateTokenException;
import com.linhx.sso.exceptions.ParseTokenException;
import com.linhx.sso.repositories.LoginHistoryRepository;
import com.linhx.sso.repositories.SequenceRepository;
import com.linhx.sso.repositories.TokenRepository;
import com.linhx.utils.JwtUtils;
import com.linhx.utils.functions.F;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linhx
 * @since 08/10/2020
 */
@Service
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
    private final EnvironmentVariable env;
    private final TokenRepository tokenRepository;
    private final SequenceRepository sequenceRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private HashSet<Long> invalidIds = null;

    private HashSet<Long> getInvalidIds() {
        if (this.invalidIds == null) {
            this.invalidIds = new HashSet<>(this.tokenRepository.findInvalidIds());
        }
        return this.invalidIds;
    }

    public JwtUtils.JwtResult generateToken(Long userId, Long loginHistoryId, F<Token, JwtUtils.JwtResult> f) throws GenerateTokenException {
        try {
            var tokenEntity = new Token(
                    this.sequenceRepository.getNextSequence(Token.SEQ_NAME),
                    userId,
                    null,
                    false,
                    loginHistoryId
            );
            this.tokenRepository.save(tokenEntity);

            var result = f.apply(tokenEntity);

            tokenEntity.setExpired(result.getExpired());
            this.tokenRepository.save(tokenEntity);
            return result;
        } catch (Exception e) {
            throw new GenerateTokenException(e);
        }
    }

    public JwtUtils.JwtResult generateAccessToken(UserDetail user, Long loginHistoryId, Long refreshTokenId) throws GenerateTokenException {
        return this.generateToken(user.getId(), loginHistoryId, token -> {
            var userStr = mapper.writeValueAsString(user);
            return JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_USER, userStr)
                            .claim(SecurityConstants.JWT_ID, token.getId())
                            .claim(SecurityConstants.JWT_REFRESH_TOKEN_ID, refreshTokenId)
                            .claim(SecurityConstants.JWT_LOGIN_HISTORY_ID, token.getLoginHistoryId()),
                    this.env.getAccessTokenSecret(),
                    SecurityConstants.TOKEN_EXPIRATION_SECONDS,
                    token.getId());
        });
    }

    public JwtUtils.JwtResult generateRefreshToken(UserDetail user, Long loginHistoryId) throws GenerateTokenException {
        return this.generateToken(user.getId(), loginHistoryId, token ->
                JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_USER, user.getId())
                                .claim(SecurityConstants.JWT_ID, token.getId())
                                .claim(SecurityConstants.JWT_LOGIN_HISTORY_ID, token.getLoginHistoryId()),
                        this.env.getRefreshTokenSecret(),
                        SecurityConstants.REFRESH_TOKEN_EXPIRATION_SECONDS,
                        token.getId())
        );
    }

    public TokenDetail parseAccessToken(String token) throws ParseTokenException {
        try {
            var claims = JwtUtils.parse(token, this.env.getAccessTokenSecret());
            var id = claims.get(SecurityConstants.JWT_ID, Long.class);
            var refreshTokenId = claims.get(SecurityConstants.JWT_REFRESH_TOKEN_ID, Long.class);
            var userStr = claims.get(SecurityConstants.JWT_USER, String.class);
            var loginHistoryId = claims.get(SecurityConstants.JWT_LOGIN_HISTORY_ID, Long.class);
            var userDetail = this.mapper.readValue(userStr, UserDetail.class);
            return new TokenDetail(id, refreshTokenId, userDetail, claims.getExpiration(), loginHistoryId);
        } catch (Exception e) {
            throw new ParseTokenException(e);
        }
    }

    public RefreshTokenDetail parseRefreshToken(String token) {
        var claims = JwtUtils.parse(token, this.env.getRefreshTokenSecret());
        var id = claims.get(SecurityConstants.JWT_ID, Long.class);
        var userId = claims.get(SecurityConstants.JWT_USER, Long.class);
        var loginHistoryId = claims.get(SecurityConstants.JWT_LOGIN_HISTORY_ID, Long.class);
        return new RefreshTokenDetail(id, userId, claims.getExpiration(), loginHistoryId);
    }

    private void invalidate(Long id, Long userId, Long loginHistoryId) {
        var tokenOpt = this.tokenRepository.findById(id);
        if (tokenOpt.isPresent()) {
            var token = tokenOpt.get();
            token.setInvalid(true);
            try {
                this.tokenRepository.save(token);
            } catch (Exception e) {
                logger.error("error invalidate token id {}", id);
            }
        } else { // insert to table token if it doesn't exist for any reason
            var missingToken = new Token(
                    id,
                    userId,
                    new Date(0),
                    true,
                    loginHistoryId
            );
            try {
                this.tokenRepository.save(missingToken);
            } catch (Exception e) {
                logger.error("error invalidate token id {}", id);
            }
        }
        this.invalidIds.add(id);
    }

    public void invalidate(RefreshTokenDetail tokenDetails) {
        this.invalidate(tokenDetails.getId(), tokenDetails.getUserId(), tokenDetails.getLh());
    }

    public void invalidate(TokenDetail tokenDetail) {
        this.invalidate(tokenDetail.getId(), tokenDetail.getUser().getId(), tokenDetail.getLh());
    }

    public void invalidateByUserId(Long userId) {
        try {
            List<Long> validIds = this.tokenRepository.findValidByUserId(userId);
            this.getInvalidIds().addAll(validIds);
        } catch (Exception e) {
            logger.error("error invalidate tokens of user {}", userId);
        }
        this.tokenRepository.invalidateByUserId(userId);
    }

    @Override
    public void invalidateByLoginHistoryId(Long loginHistoryId) {
        try {
            List<Long> validIds = this.tokenRepository.findValidByLoginHistoryId(loginHistoryId);
            this.getInvalidIds().addAll(validIds);
        } catch (Exception e) {
            logger.error("error invalidate tokens of login history id {}", loginHistoryId);
        }
        this.tokenRepository.invalidateByLoginHistoryId(loginHistoryId);
    }

    public boolean isInvalid(Long id) {
        return this.getInvalidIds().contains(id);
    }

    public void deleteExpiredTokens() {
        var loginHistoryIds = new ArrayList<Long>();
        try {
            List<Token> expiredTokens = this.tokenRepository.findExpiredTokens();
            for (Token token : expiredTokens) {
                this.getInvalidIds().remove(token.getId());
                loginHistoryIds.add(token.getLoginHistoryId());
            }
        } catch (Exception e) {
            logger.error("error remove expired tokens id from cache");
        }
        if (!loginHistoryIds.isEmpty()) {
            this.loginHistoryRepository.deleteAllById(loginHistoryIds);
        }
        this.tokenRepository.deleteExpiredTokens();
    }

    /**
     * Gets the authorities.
     *
     * @return the authorities
     */
    public Collection<? extends GrantedAuthority> getAuthorities(UserDetail user) {
        return getGrantedAuthorities(new ArrayList<>()); // TODO
    }

    /**
     * Gets the granted authorities.
     *
     * @param privileges the privileges
     * @return the granted authorities
     */
    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
