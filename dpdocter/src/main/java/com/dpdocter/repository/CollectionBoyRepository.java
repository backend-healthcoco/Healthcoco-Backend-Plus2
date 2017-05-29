package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CollectionBoyCollection;

public interface CollectionBoyRepository extends MongoRepository<CollectionBoyCollection, ObjectId>{

	@Query(value = "{ 'locationId': ?0}", count = true)
	Integer getCount(ObjectId locationId);
	
}
