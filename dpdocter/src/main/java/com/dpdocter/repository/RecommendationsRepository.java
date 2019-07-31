package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RecommendationsCollection;

public interface RecommendationsRepository extends MongoRepository<RecommendationsCollection, ObjectId> {

	RecommendationsCollection findByDoctorIdAndLocationIdAndPatientId(ObjectId doctorId, ObjectId locationId,
			ObjectId patientId);

}
