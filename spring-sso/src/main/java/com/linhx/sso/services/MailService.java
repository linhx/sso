package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.exceptions.EmailException;

import java.util.List;

/**
 * MailService
 *
 * @author linhx
 * @since 08/09/2021
 */
public interface MailService {
    /**
     * Send an email
     *
     * @param emailFrom  the from email address
     * @param emailTo    the destination email address
     * @param subjectStr the subject
     * @param contentStr the content
     * @throws Exception
     */
    void send(String emailFrom, String emailTo, String subjectStr, String contentStr) throws EmailException;

    void adminSend(String emailTo, String subjectStr, String contentStr) throws EmailException;

    /**
     * Send an email
     *
     * @param emailFrom  the from email address
     * @param emailTo    the destination email address
     * @param subjectStr the subject
     * @param contentStr the content
     * @throws Exception
     */
    void send(String emailFrom, List<String> emailTo, String subjectStr, String contentStr) throws BaseException;
}
