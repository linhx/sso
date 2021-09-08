package com.linhx.sso.services.impls;

import com.linhx.exceptions.ResetPasswordException;
import com.linhx.sso.constants.SecurityConstants;
import com.linhx.sso.entities.ResetPasswordRequest;
import com.linhx.sso.entities.User;
import com.linhx.sso.enums.ResetPasswordRequestStatus;
import com.linhx.sso.exceptions.SequenceException;
import com.linhx.sso.repositories.ResetPasswordRequestRepository;
import com.linhx.sso.services.ResetPasswordRequestService;
import com.linhx.utils.HashingUtils;
import org.bson.BsonTimestamp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ResetPasswordRequestServiceImpl
 *
 * @author linhx
 * @since 08/09/2021
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ResetPasswordRequestServiceImpl implements ResetPasswordRequestService {
    private final ResetPasswordRequestRepository resetPasswordRequestRepo;

    public ResetPasswordRequestServiceImpl(ResetPasswordRequestRepository resetPasswordRequestRepo) {
        this.resetPasswordRequestRepo = resetPasswordRequestRepo;
    }

    @Override
    public Optional<ResetPasswordRequest> findByToken(String token) {
        return this.resetPasswordRequestRepo.findByToken(token);
    }

    @Override
    public List<ResetPasswordRequest> deleteByUserId(Long userId) {
        List<ResetPasswordRequest> oldRestPasswordRequest = this.resetPasswordRequestRepo.findByUserId(userId);
        if (oldRestPasswordRequest != null) {
            for (ResetPasswordRequest r : oldRestPasswordRequest) {
                r.setStatus(ResetPasswordRequestStatus.DELETED.getValue());
                this.resetPasswordRequestRepo.save(r);
            }
        }
        return oldRestPasswordRequest;
    }

    @Override
    public ResetPasswordRequest create(User user) throws ResetPasswordException {
        String randomBase64Url;
        boolean isExisted;
        int generateTime = 0;
        do {
            if (generateTime > SecurityConstants.MAX_GENERATE_RESET_PASSWORD_TOKEN_TIMES) {
                throw new ResetPasswordException("error.resetPassword.tryTooMuch");
            }
            generateTime++;
            randomBase64Url = HashingUtils.randomBase64Url();
            // check exist in data base
            Optional<ResetPasswordRequest> resetPasswordRequestOpt = this.findByToken(randomBase64Url);
            isExisted = resetPasswordRequestOpt.isPresent() &&
                    resetPasswordRequestOpt.get().getExpired().compareTo(new BsonTimestamp(System.currentTimeMillis())) > 0 &&
                    resetPasswordRequestOpt.get().isActive();
        } while (isExisted);

        // Delete old request of the user
        this.deleteByUserId(user.getId());

        ResetPasswordRequest entity = new ResetPasswordRequest();
        entity.setUserId(user.getId());
        entity.setToken(randomBase64Url);
        entity.setStatus(ResetPasswordRequestStatus.ACTIVE.getValue());
        entity.setExpired(new BsonTimestamp(System.currentTimeMillis() + SecurityConstants.RESET_PASSWORD_TOKEN_EXPIRED_TIME * 1000));
        try {
            return this.resetPasswordRequestRepo.insert(entity);
        } catch (SequenceException e) {
            throw new ResetPasswordException("error.resetPassword.cantCreateRequest");
        }
    }

    @Override
    public ResetPasswordRequest delete(ResetPasswordRequest entity) {
        return this.resetPasswordRequestRepo.delete(entity);
    }
}
