package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.GeneralExamCollection;

public interface GeneralExamRepository extends MongoRepository<GeneralExamCollection, ObjectId> , PagingAndSortingRepository<GeneralExamCollection, ObjectId>{

	
	
}
