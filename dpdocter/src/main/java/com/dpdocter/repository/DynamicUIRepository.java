package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DynamicUICollection;

public interface DynamicUIRepository extends MongoRepository<DynamicUICollection, ObjectId>{

	public DynamicUICollection findByDoctorId(ObjectId doctorId);
	
}
