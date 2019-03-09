package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OfferCollection;

public interface OfferRepository extends MongoRepository<OfferCollection, ObjectId> {

	@Query(value = "{'offerCode': ?0}", count = true)
	public Integer countByCode(String offerCode);
}
