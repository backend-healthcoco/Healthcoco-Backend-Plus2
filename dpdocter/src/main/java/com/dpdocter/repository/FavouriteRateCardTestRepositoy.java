package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.FavouriteRateCardTestCollection;

@Repository
public interface FavouriteRateCardTestRepositoy extends MongoRepository<FavouriteRateCardTestCollection, ObjectId> {
	FavouriteRateCardTestCollection findByLocationIdAndHospitalIdAndDiagnosticTestId(ObjectId locationId, ObjectId hospitalId,
			ObjectId diagnosticTestId);
}
