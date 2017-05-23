package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabTestPickupCollection;

public interface LabTestPickupRepository extends MongoRepository<LabTestPickupCollection, ObjectId>{

	@Query("{'requestId' : ?0}")
	LabTestPickupCollection getByRequestId(String requestId);
	
}
