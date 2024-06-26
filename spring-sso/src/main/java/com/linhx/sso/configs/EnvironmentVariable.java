package com.linhx.sso.configs;

import com.linhx.sso.constants.Paths;
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
    @Value("${security.jwt.llhs-secret}")
    private String llhsSecret;
    @Value("${security.jwt.refresh-token-secret}")
    private String refreshTokenSecret;
    @Value("${security.domain}")
    private String securityDomain;
    @Value("${security.sso.base-url}")
    private String securitySsoBaseUrl;
    @Value("${security.login-attempts.failed-allowed}")
    private Integer securityLoginAttemptsFailedAllowed;
    @Value("${security.login-attempts.time-blocking}")
    private Integer securityLoginAttemptsTimeBlocking;
    @Value("${mail.sendgrid.key}")
    private String sendgridApiKey;

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

    public String getResetPasswordUrl(String token) {
        return StringUtils.joinUrl(this.securitySsoBaseUrl, Paths.RESET_PASSWORD, token);
    }
}