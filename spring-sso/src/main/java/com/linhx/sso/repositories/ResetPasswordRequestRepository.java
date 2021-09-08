package com.linhx.sso.repositories;

import com.linhx.sso.entities.ResetPasswordRequest;
import com.linhx.sso.exceptions.SequenceException;

import java.util.List;
import java.util.Optional;

/**
 * ResetPasswordRequestRepository
 *
 * @author linhx
 * @since 08/09/2021
 */
public interface ResetPasswordRequestRepository {
    ResetPasswordRequest insert(ResetPasswordRequest entity) throws SequenceException;

    ResetPasswordRequest save(ResetPasswordRequest entity);

    Optional<ResetPasswordRequest> findByToken (String token);

    List<ResetPasswordRequest> findByUserId (Long userId);

    ResetPasswordRequest delete(ResetPasswordRequest entity);
}
