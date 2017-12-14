package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DataDynamicUICollection;

public interface DataDynamicUIRepository extends MongoRepository<DataDynamicUICollection, ObjectId>{

	  @Query("{'doctorId': ?0}")
	  DataDynamicUICollection findByDoctorId(ObjectId doctorId);
	
}
