package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AcadamicProfileCollection;

public interface AcadamicProfileRespository extends MongoRepository<AcadamicProfileCollection, ObjectId> {

	@Query(value = "{'userId': ?0}", count = true)
	Integer countByUserId(ObjectId userId);
	
	AcadamicProfileCollection findByUserId(ObjectId userId);
}
