package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RecommendationsCollection;

public interface RecommendationsRepository extends MongoRepository<RecommendationsCollection, ObjectId> {

	@Query("{'doctorClinicProfileId': ?0,'patientId':?1}")
	RecommendationsCollection findByDoctorClinicProfileIdAndPatientId(ObjectId doctorClinicProfileId,
			ObjectId patientId);

}
