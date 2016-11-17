package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RecommendationsCollection;

public interface RecommendationsRepository extends MongoRepository<RecommendationsCollection, ObjectId> {

	@Query("{'doctorId': ?0,'locationId':?1,'patientId':?2}")
	RecommendationsCollection findByDoctorIdLocationIdAndPatientId(ObjectId doctorId, ObjectId locationId,
			ObjectId patientId);

}
