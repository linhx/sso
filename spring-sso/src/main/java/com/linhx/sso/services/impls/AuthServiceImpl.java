package com.linhx.sso.services.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.controller.dtos.request.AuthDto;
import com.linhx.sso.controller.dtos.response.PrincipalDto;
import com.linhx.sso.exceptions.LoginInfoWrongException;
import com.linhx.sso.services.AuthService;
import com.linhx.sso.services.ClientApplicationService;
import com.linhx.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

/**
 * AuthServiceImpl
 *
 * @author linhx
 * @since 08/11/2020
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final ClientApplicationService clientApplicationService;

    public AuthServiceImpl(ClientApplicationService clientApplicationService) {
        this.clientApplicationService = clientApplicationService;
    }

    @Override
    public PrincipalDto auth(AuthDto dto) throws AuthenticationException, BaseException {
        var clientAppOpt = this.clientApplicationService.findByClientIdAndSecret(dto.getClientId(), dto.getClientSecret());
        if (clientAppOpt.isEmpty()) {
            throw new LoginInfoWrongException("error.auth.clientAppDoesNotExist");
        }
        var clientApp = clientAppOpt.get();

        try {
            var claims = JwtUtils.parse(dto.getAccessToken(), clientApp.getAccessTokenSecret());
            var username = claims.get(SecurityConstants.JWT_USERNAME, String.class);
            return new PrincipalDto(username);
        } catch (Exception e) {
            throw new LoginInfoWrongException("error.auth.invalidAccessToken");
        }
    }
}
