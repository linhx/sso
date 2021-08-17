package com.linhx.sso.repositories.impls;

import com.linhx.exceptions.BaseException;
import com.linhx.exceptions.ResourceNotFoundException;
import com.linhx.sso.entities.Sequence;
import com.linhx.sso.repositories.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public Long getNextSequence(String seqName) throws BaseException {
        var sequence = mongoOperations.findAndModify(
                query(where("seqName").is(seqName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                Sequence.class
        );
        if (sequence != null) {
            return sequence.getSeq();
        }
        throw new ResourceNotFoundException(String.format("sequence %s not found", seqName));
    }
}
