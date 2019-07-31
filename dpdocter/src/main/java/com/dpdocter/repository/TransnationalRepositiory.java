package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TransactionalCollection;

public interface TransnationalRepositiory extends MongoRepository<TransactionalCollection, ObjectId> {

    TransactionalCollection findByResourceIdAndResource(ObjectId resourceId, String resource);

    List<TransactionalCollection> findByIsCached(boolean isCached);

}
