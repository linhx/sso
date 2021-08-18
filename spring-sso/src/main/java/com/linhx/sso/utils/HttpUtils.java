package com.linhx.sso.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpUtils
 *
 * @author linhx
 * @since 18/08/2021
 */
public class HttpUtils {
    public static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
}
