package com.dpdocter.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.FlowsheetCollection;

public interface FlowsheetRepository extends MongoRepository<FlowsheetCollection, ObjectId>{

	
	@Query("{'dischargeSummaryId': ?0}")
    public FlowsheetCollection findByDischargeSummaryId(ObjectId dischargeSummaryId);
}
