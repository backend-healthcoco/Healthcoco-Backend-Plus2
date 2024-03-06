package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnFetchModeCollection;

public interface OnFetchModeRepository extends MongoRepository<OnFetchModeCollection, ObjectId> {

	OnFetchModeCollection findByRespRequestId(String requestId);

}
