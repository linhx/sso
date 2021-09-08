package com.linhx.sso.services;

import com.linhx.exceptions.ResetPasswordException;
import com.linhx.sso.entities.ResetPasswordRequest;
import com.linhx.sso.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * ResetPasswordRequestService
 *
 * @author linhx
 * @since 08/09/2021
 */
public interface ResetPasswordRequestService {
    Optional<ResetPasswordRequest> findByToken(String token);

    List<ResetPasswordRequest> deleteByUserId(Long userId);

    ResetPasswordRequest create(User user) throws ResetPasswordException;

    ResetPasswordRequest delete(ResetPasswordRequest entity);
}
