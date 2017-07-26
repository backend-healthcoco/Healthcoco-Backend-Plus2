package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PresentingComplaintThroatCollection;

public interface PresentingComplaintThroatRepository extends MongoRepository<PresentingComplaintThroatCollection, ObjectId>{

	
	
}
