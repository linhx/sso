package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.response.PrincipalDto;

import javax.naming.AuthenticationException;

/**
 * AuthService
 *
 * @author linhx
 * @since 08/11/2020
 */
public interface AuthService {
    PrincipalDto auth(AuthDto dto) throws AuthenticationException, BaseException;
}
