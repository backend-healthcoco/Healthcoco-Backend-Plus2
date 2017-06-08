package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RateCardCollection;

public interface RateCardRepository extends MongoRepository<RateCardCollection, ObjectId>{

	@Query("{'locationId': ?0 , 'isDefault': true}")
	public RateCardCollection getDefaultRateCard(ObjectId locationId);
}
