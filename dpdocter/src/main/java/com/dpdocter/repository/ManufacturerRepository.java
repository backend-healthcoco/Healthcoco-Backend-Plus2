package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ManufacturerCollection;

public interface ManufacturerRepository extends MongoRepository<ManufacturerCollection, ObjectId>{

	
	
}
