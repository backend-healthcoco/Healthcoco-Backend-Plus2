package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RateCardLabAssociationCollection;

public interface RateCardLabAssociationRepository extends MongoRepository<RateCardLabAssociationCollection, ObjectId>{

	@Query("{'daughterLabId': ?0 , 'parentLabId' : ?1,'rateCardId': ?2}")
	public RateCardLabAssociationCollection getByLocationAndRateCard(ObjectId daughterLabId, ObjectId parentLabId, ObjectId rateCardId);
	
	
	@Query("{'daughterLabId': ?0 , 'parentLabId' : ?1}")
	public RateCardLabAssociationCollection getByLocation(ObjectId daughterLabId , ObjectId parentLabId);
	
}
