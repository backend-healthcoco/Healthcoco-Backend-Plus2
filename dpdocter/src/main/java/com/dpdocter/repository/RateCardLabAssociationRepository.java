package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RateCardLabAssociationCollection;

public interface RateCardLabAssociationRepository extends MongoRepository<RateCardLabAssociationCollection, ObjectId>{

	@Query("{'locationId': ?0,'rateCardId': ?1}")
	public RateCardLabAssociationCollection getByLocationAndRateCard(ObjectId locationId, ObjectId rateCardId);
	
	
	@Query("{'locationId': ?0}")
	public RateCardLabAssociationCollection getByLocation(ObjectId locationId);
	
}
