package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PresentComplaintCollection;

public interface PresentComplaintRepository extends MongoRepository<PresentComplaintCollection, ObjectId> , PagingAndSortingRepository<PresentComplaintCollection, ObjectId> {

	
	
}
