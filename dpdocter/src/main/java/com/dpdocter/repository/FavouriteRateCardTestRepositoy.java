package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.FavouriteRateCardTestCollection;

@Repository
public interface FavouriteRateCardTestRepositoy extends MongoRepository<FavouriteRateCardTestCollection, ObjectId> {
	@Query(value = "{'locationId' : ?0,'hospitalId': ?1,'diagnosticTestId': ?2}")
	FavouriteRateCardTestCollection findByLocationIdHospitalIdAndTestId(ObjectId locationId, ObjectId hospitalId,
			ObjectId diagnosticTestId);
}
