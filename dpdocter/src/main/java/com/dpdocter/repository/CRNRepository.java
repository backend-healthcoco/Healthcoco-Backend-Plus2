package com.dpdocter.repository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CRNCollection;

public interface CRNRepository extends MongoRepository<CRNCollection, ObjectId>{

	CRNCollection findByLocationIdAndCrnNumberAndRequestId(ObjectId locationId, String crn , String requestId);
	
}
