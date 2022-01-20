package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.NotImplemented;
import com.linhx.sso.configs.EnvironmentVariable;
import com.linhx.sso.configs.MailConfig;
import com.linhx.sso.exceptions.EmailException;
import com.linhx.sso.services.MailService;
import com.sendgrid.*;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * MailServiceImpl
 *
 * @author linhx
 * @since 08/09/2021
 */
@Service
public class MailServiceImpl implements MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    private final MailConfig mailConfig;
    private final EnvironmentVariable env;

    public MailServiceImpl(MailConfig mailConfig, EnvironmentVariable env) {
        this.mailConfig = mailConfig;
        this.env = env;
    }

    @Override
    public void send(String emailFrom, String emailTo, String subjectStr, String contentStr) throws EmailException {
        Email from = new Email(emailFrom);
        Email to = new Email(emailTo);
        Content content = new Content(ContentType.TEXT_HTML.getMimeType(), contentStr);
        Mail mail = new Mail(from, subjectStr, to, content);

        SendGrid sg = new SendGrid(this.env.getSendgridApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw new EmailException("error.mail.cantSend", ex);
        }
    }

    @Override
    public void adminSend(String emailTo, String subjectStr, String contentStr) throws EmailException {
        this.send(this.mailConfig.getAdminEmail(), emailTo, subjectStr, contentStr);
    }

    @Override
    public void send(String emailFrom, List<String> emailTo, String subjectStr, String contentStr) throws BaseException {
        throw new NotImplemented();
    }
}
