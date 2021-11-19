package com.linhx.sso.configs;

import com.linhx.exceptions.ResetPasswordException;
import com.linhx.utils.StringUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

import java.util.HashMap;

/**
 * MailConfig
 *
 * @author linhx
 * @since 08/09/2021
 */
@Getter
@Configuration
@PropertySource(value = "classpath:emailconfig.properties", encoding = "UTF-8")
public class MailConfig {
    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);

    @Value("${email.admin}")
    private String adminEmail;
    @Value("${email.resetpassword.subject}")
    private String resetPasswordSubject;
    @Value("${email.resetpassword.content.filepath}")
    private String resetPasswordContentFilePath;

	private final ResourceLoader resourceLoader;

	public ResourceApplication(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

    public String getResetPasswordContent(String fullName, String url) throws ResetPasswordException {
        try {
            var mailContentResource = resourceLoader.getResource((this.resetPasswordContentFilePath);
            try (var mailContentIs = mailContentResource.getInputStream()) {
                var params = new HashMap<String, Object>();
                params.put("fullName", fullName);
                params.put("url", url);
                return StringUtils.format(mailContentIs, params);
            }
        } catch (Exception e) {
            logger.error("error.resetPassword.cantCreateMail", e);
            throw new ResetPasswordException("error.resetPassword.cantCreateMail");
        }
    }
}
