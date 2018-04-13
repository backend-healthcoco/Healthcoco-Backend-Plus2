package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalImagingCollection;

public interface DentalImagingRepository extends MongoRepository<DentalImagingCollection, ObjectId>{

	
	
}
