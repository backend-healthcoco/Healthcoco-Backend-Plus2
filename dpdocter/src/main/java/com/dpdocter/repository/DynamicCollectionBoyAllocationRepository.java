package com.dpdocter.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DynamicCollectionBoyAllocationCollection;

public interface DynamicCollectionBoyAllocationRepository extends MongoRepository<DynamicCollectionBoyAllocationCollection, ObjectId>{
	
	@Query("{'assignorId':?0 , 'assigneeId':?1 }")
	public DynamicCollectionBoyAllocationCollection findByAssignorIdAndAssigneeId( ObjectId assignorId , ObjectId assigneeId);

}
