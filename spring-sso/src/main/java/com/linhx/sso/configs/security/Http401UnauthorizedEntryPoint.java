package com.linhx.sso.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhx.exceptions.message.Message;
import com.linhx.sso.controller.dtos.response.MessagesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Http401UnauthorizedEntryPoint
 *
 * @author linhx
 * @since 18/08/2021
 */
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);

    /**
     * Always returns a 401 error code to the client.
     */
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException arg2) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Pre-authenticated entry point called. Rejecting access");
        }
        var errorMsg = Message.error("error.auth.unauthorized").build();
        ObjectMapper mapper = new ObjectMapper();
        String tokensStr = mapper.writeValueAsString(MessagesDto.fromMessage(errorMsg));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(tokensStr);
        out.flush();
    }
}
