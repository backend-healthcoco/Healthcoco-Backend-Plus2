package com.dpdocter.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;

public interface RateCardDentalWorkAssociationRepository extends MongoRepository<RateCardDentalWorkAssociationCollection, ObjectId> {

	@Query("{'locationId': ?0 , 'dentalWorkId' : ?1 , 'rateCardId' :?2}")
	public RateCardDentalWorkAssociationCollection getByLocationWorkRateCard(ObjectId locationId , ObjectId dentalWorkId ,ObjectId rateCardId);
	
}
