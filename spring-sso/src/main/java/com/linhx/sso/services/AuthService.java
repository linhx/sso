package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.BusinessException;
import com.linhx.sso.configs.security.Tokens;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.response.PrincipalDto;
import com.linhx.sso.entities.LogoutByLoginHistoryScheduler;
import com.linhx.sso.exceptions.RefreshTokenAlreadyUsedException;

import javax.naming.AuthenticationException;

/**
 * AuthService
 *
 * @author linhx
 * @since 08/11/2020
 */
public interface AuthService {
    int SCHEDULE_TIME_LOGOUT = 30; // 30 seconds

    PrincipalDto auth(AuthDto dto) throws AuthenticationException, BaseException;

    Tokens refresh(String refreshToken) throws RefreshTokenAlreadyUsedException;

    String createLlhsJwt(Long llhsId) throws Exception;

    Long parseLlhsJwt(String llshJwt);

    LogoutByLoginHistoryScheduler logoutScheduler(Long loginHistoryId) throws BusinessException;

    boolean cancelLogoutByLoginHistoryScheduler(Long logoutByLoginHistorySchedulerId);
}
