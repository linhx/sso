package com.linhx.sso.repositories;

import com.linhx.exceptions.BaseException;

/**
 * SequenceRepository
 *
 * @author linhx
 * @since 12/11/2020
 */
public interface SequenceRepository {
    Long getNextSequence(String seqName) throws BaseException;
}
