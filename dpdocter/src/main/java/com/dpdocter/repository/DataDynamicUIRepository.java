package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DataDynamicUICollection;

public interface DataDynamicUIRepository extends MongoRepository<DataDynamicUICollection, ObjectId>{

	  DataDynamicUICollection findByDoctorId(ObjectId doctorId);
	
}
