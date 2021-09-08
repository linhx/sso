package com.linhx.sso.services;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.BusinessException;
import com.linhx.exceptions.ResetPasswordException;
import com.linhx.sso.controller.dtos.request.ResetPasswordDto;
import com.linhx.sso.controller.dtos.request.ResetPasswordRequestDto;
import com.linhx.sso.entities.LoginHistory;
import com.linhx.sso.entities.User;

import java.util.Optional;

/**
 * IUserService
 *
 * @author linhx
 * @since 28/10/2020
 */
public interface UserService {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    Optional<User> findByUuid(String uuid);

    /**
     * Save user
     *
     * @param user the user
     * @return the user object
     * @throws BaseException exception
     */
    User save(User user);

    LoginHistory createLoginHistory(Long userId) throws BaseException;

    /**
     * Request reset password
     *
     * @param resetPasswordRequestDto the dto
     */
    void requestResetPassword(ResetPasswordRequestDto resetPasswordRequestDto) throws BusinessException;

    /**
     * Reset password
     *
     * @param resetPasswordDto
     * @throws BaseException
     */
    void resetPassword(ResetPasswordDto resetPasswordDto) throws ResetPasswordException;

    void changePassword(User user, String newPassword);
}
