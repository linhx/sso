package com.linhx.sso.exceptions;

import com.linhx.exceptions.BaseException;
import lombok.Getter;

/**
 * TokenAlreadyUsedException
 *
 * @author linhx
 * @since 07/09/2021
 */
@Getter
public class RefreshTokenAlreadyUsedException extends BaseException {
    private Long loginHistoryId;

    public RefreshTokenAlreadyUsedException(Long loginHistoryId) {
        super("error.refreshToken.alreadyUsed");
        this.loginHistoryId = loginHistoryId;
    }
}