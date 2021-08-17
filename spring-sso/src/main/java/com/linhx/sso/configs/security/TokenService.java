package com.linhx.sso.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.Token;
import com.linhx.sso.repositories.SequenceRepository;
import com.linhx.sso.repositories.TokenRepository;
import com.linhx.utils.DateTimeUtils;
import com.linhx.utils.JwtUtils;
import com.linhx.utils.functions.F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author linhx
 * @since 08/10/2020
 */
@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final EnvironmentVariable env;
    private final TokenRepository tokenRepository;
    private final SequenceRepository sequenceRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private HashSet<Long> invalidIds = null;

    public TokenService(EnvironmentVariable env, TokenRepository tokenRepository, SequenceRepository sequenceRepository) {
        this.env = env;
        this.tokenRepository = tokenRepository;
        this.sequenceRepository = sequenceRepository;
    }

    private HashSet<Long> getInvalidIds() {
        if (this.invalidIds == null) {
            this.invalidIds = new HashSet<>(this.tokenRepository.findInvalidIds());
        }
        return this.invalidIds;
    }

    public JwtUtils.JwtResult generateToken(Long userId, F<Token, JwtUtils.JwtResult> f) throws Exception {
        var tokenEntity = new Token();
        tokenEntity.setId(this.sequenceRepository.getNextSequence(Token.SEQ_NAME));
        tokenEntity.setUserId(userId);
        tokenEntity.setInvalid(false);
        this.tokenRepository.save(tokenEntity);

        var result = f.apply(tokenEntity);

        tokenEntity.setExpired(result.getExpired());
        this.tokenRepository.save(tokenEntity);
        return result;
    }

    public JwtUtils.JwtResult generateAccessToken(UserDetail user, Long refreshTokenId) throws Exception {
        return this.generateToken(user.getId(), token -> {
            var userStr = mapper.writeValueAsString(user);
            return JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_USER, userStr)
                            .claim(SecurityConstants.JWT_ID, token.getId())
                            .claim(SecurityConstants.JWT_REFRESH_TOKEN_ID, refreshTokenId),
                    this.env.getAccessTokenSecret(),
                    SecurityConstants.TOKEN_EXPIRATION_SECONDS,
                    token.getId());
        });
    }

    public JwtUtils.JwtResult generateRefreshToken(UserDetail userDetails) throws Exception {
        return this.generateToken(userDetails.getId(), token ->
                JwtUtils.generate(builder -> builder.claim(SecurityConstants.JWT_USER, userDetails.getId())
                                .claim(SecurityConstants.JWT_ID, token.getId()),
                        this.env.getRefreshTokenSecret(),
                        SecurityConstants.REFRESH_TOKEN_EXPIRATION_SECONDS,
                        token.getId())
        );
    }

    public TokenDetail parseAccessToken(String token) throws Exception {
        var claims = JwtUtils.parse(token, this.env.getAccessTokenSecret());
        var id = claims.get(SecurityConstants.JWT_ID, Long.class);
        var refreshTokenId = claims.get(SecurityConstants.JWT_REFRESH_TOKEN_ID, Long.class);
        var userStr = claims.get(SecurityConstants.JWT_USER, String.class);
        var userDetails = this.mapper.readValue(userStr, UserDetail.class);
        return new TokenDetail(id, refreshTokenId, userDetails, claims.getExpiration());
    }

    public RefreshTokenDetail parseRefreshToken(String token) {
        var claims = JwtUtils.parse(token, this.env.getRefreshTokenSecret());
        var id = claims.get(SecurityConstants.JWT_ID, Long.class);
        var userId = claims.get(SecurityConstants.JWT_USER, Long.class);
        return new RefreshTokenDetail(id, userId, claims.getExpiration());
    }

    public void invalidate(Long id, Long userId, Date expired) {
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
            var missingToken = new Token();
            missingToken.setId(id);
            missingToken.setInvalid(true);
            missingToken.setExpired(expired != null ? expired :
                    DateTimeUtils.dateFromNow(SecurityConstants.MAX_TOKENS_EXPIRATION_SECONDS));
            missingToken.setUserId(userId);
            try {
                this.tokenRepository.save(missingToken);
            } catch (Exception e) {
                logger.error("error invalidate token id {}", id);
            }
        }
        this.invalidIds.add(id);
    }

    public void invalidate(Long id) {
        this.invalidate(id, null, null);
    }

    public void invalidate(RefreshTokenDetail tokenDetails) {
        this.invalidate(tokenDetails.getId(), tokenDetails.getUserId(), tokenDetails.getExpired());
    }

    public void invalidate(TokenDetail tokenDetails) {
        this.invalidate(tokenDetails.getId(), tokenDetails.getUser().getId(), tokenDetails.getExpired());
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

    public boolean isInvalid(Long id) {
        return this.getInvalidIds().contains(id);
    }

    public void deleteExpiredTokens() {
        try {
            List<Long> expiredIds = this.tokenRepository.findExpiredTokens();
            this.getInvalidIds().removeAll(expiredIds);
        } catch (Exception e) {
            logger.error("error remove expired tokens id");
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
