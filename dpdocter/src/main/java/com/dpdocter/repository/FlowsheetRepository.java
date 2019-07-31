package com.dpdocter.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FlowsheetCollection;

public interface FlowsheetRepository extends MongoRepository<FlowsheetCollection, ObjectId>{

	public FlowsheetCollection findByDischargeSummaryId(ObjectId dischargeSummaryId);
}
