package com.linhx.sso.configs;

import com.linhx.exceptions.ResetPasswordException;
import com.linhx.utils.StringUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
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
    @Value("${email.admin}")
    private String adminEmail;
    @Value("${email.resetpassword.subject}")
    private String resetPasswordSubject;
    @Value("${email.resetpassword.content.filepath}")
    private String resetPasswordContentFilePath;

    public String getResetPasswordContent(String fullName, String url) throws ResetPasswordException {
        try {
            File mailContentFile = ResourceUtils.getFile(this.resetPasswordContentFilePath);
            try (var mailContentIs = new FileInputStream(mailContentFile)) {
                var params = new HashMap<String, Object>();
                params.put("fullName", fullName);
                params.put("url", url);
                return StringUtils.format(mailContentIs, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResetPasswordException("error.resetPassword.cantCreateMail");
        }
    }
}
