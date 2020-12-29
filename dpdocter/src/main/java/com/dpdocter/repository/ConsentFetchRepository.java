package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ConsentFetchRequestCollection;

public interface ConsentFetchRepository extends MongoRepository<ConsentFetchRequestCollection, ObjectId> {

	ConsentFetchRequestCollection findByRespRequestId(String requestId);

}
