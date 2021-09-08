package com.linhx.sso.repositories.impls;

import com.linhx.sso.entities.Sequence;
import com.linhx.sso.exceptions.SequenceException;
import com.linhx.sso.repositories.SequenceRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {
    private final MongoOperations mongoOperations;

    public SequenceRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Long getNextSequence(String seqName) throws SequenceException {
        var sequence = mongoOperations.findAndModify(
                query(where("seqName").is(seqName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                Sequence.class
        );
        if (sequence != null) {
            return sequence.getSeq();
        }
        throw new SequenceException(String.format("sequence %s not found", seqName));
    }
}
