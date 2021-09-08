package com.linhx.sso.enums;

/**
 * ResetPasswordRequestStatus
 *
 * @author linhx
 * @since 08/09/2021
 */
public enum ResetPasswordRequestStatus {
    ACTIVE(0), DELETED(1);
    private int value;
    ResetPasswordRequestStatus(int value) {
        this.value = value;
    }
    public int getValue () {
        return this.value;
    }
}
