package com.linhx.sso.repositories;

/**
 * SequenceRepository
 *
 * @author linhx
 * @since 12/11/2020
 */
public interface SequenceRepository {
    Long getNextSequence(String seqName);
}
