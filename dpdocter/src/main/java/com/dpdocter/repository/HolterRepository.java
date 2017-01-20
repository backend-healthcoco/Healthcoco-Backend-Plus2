package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.HolterCollection;

public interface HolterRepository extends MongoRepository<HolterCollection, ObjectId>{
	
	

}
