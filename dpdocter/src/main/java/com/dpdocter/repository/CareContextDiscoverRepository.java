package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CareContextDiscoverCollection;

public interface CareContextDiscoverRepository extends MongoRepository<CareContextDiscoverCollection, ObjectId>{

	CareContextDiscoverCollection findByRequestId(String requestId);

}
