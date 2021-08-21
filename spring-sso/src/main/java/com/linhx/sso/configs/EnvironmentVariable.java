package com.linhx.sso.configs;

import com.linhx.utils.StringUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * EnvironmentVariable
 *
 * @author linhx
 */
@Component
@Getter
public class EnvironmentVariable {
    @Value("${security.cors.origins}")
    private String corsOrigins;
    @Value("${security.cors.methods}")
    private String corsMethods;
    @Value("${security.jwt.access-token-secret}")
    private String accessTokenSecret;
    @Value("${security.jwt.refresh-token-secret}")
    private String refreshTokenSecret;
    @Value("${security.jwt.request-access-token-secret}")
    private String requestAccessTokenSecret;
    @Value("${security.domain}")
    private String securityDomain;
    @Value("${security.login-attempts.failed-allowed}")
    private Integer securityLoginAttemptsFailedAllowed;
    @Value("${security.login-attempts.time-blocking}")
    private Integer securityLoginAttemptsTimeBlocking;

    public String[] getCorsOrigins() {
        if (StringUtils.isExist(this.corsOrigins)) {
            return this.corsOrigins.split(",");
        }
        return new String[]{};
    }

    public String[] getCorsMethods() {
        if (StringUtils.isExist(this.corsMethods)) {
            return this.corsMethods.split(",");
        }
        return new String[]{};
    }
}