package com.dpdocter.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;

public interface RateCardDentalWorkAssociationRepository extends MongoRepository<RateCardDentalWorkAssociationCollection, ObjectId> {

	public RateCardDentalWorkAssociationCollection findByLocationIdAndDentalWorkIdAndRateCardId(ObjectId locationId , ObjectId dentalWorkId ,ObjectId rateCardId);
	
}
