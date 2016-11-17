package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DynamicUICollection;

public interface DynamicUIRepository extends MongoRepository<DynamicUICollection, ObjectId>{

	@Query("{'doctorId': ?0}")
	public DynamicUICollection findByDoctorId(ObjectId doctorId);
	
}
