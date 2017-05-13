package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CollectionBoyCollection;

public interface CollectionBoyRepository extends MongoRepository<CollectionBoyCollection, ObjectId>{

	
	
}
