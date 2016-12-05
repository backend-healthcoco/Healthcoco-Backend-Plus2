package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BirthHistoryCollection;

public interface BirthHistoryRepository extends MongoRepository<BirthHistoryCollection, ObjectId> {

	@Query("{'patientId': ?0}")
	public BirthHistoryCollection findByPatientId(ObjectId patientId);
	
}
