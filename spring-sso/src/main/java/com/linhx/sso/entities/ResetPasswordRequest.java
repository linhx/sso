package com.linhx.sso.entities;

import com.linhx.sso.enums.ResetPasswordRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.BsonTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

/**
 * ResetPasswordRequest
 *
 * @author linhx
 * @date 09/04/2020
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest extends Base {
    @Transient
    public static final String SEQ_NAME = ResetPasswordRequest.class.getName();

    @Id
    Long id;
    Long userId;
    String token;
    BsonTimestamp expired;
    int status;

    public boolean isActive() {
        return ResetPasswordRequestStatus.ACTIVE.getValue() == this.status;
    }
}
