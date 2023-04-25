package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RateCardCollection;

public interface RateCardRepository extends MongoRepository<RateCardCollection, ObjectId> {

	public RateCardCollection findByLocationIdAndIsDefaultIsTrue(ObjectId locationId);
}
