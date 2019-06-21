package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientLifeStyleCollection;

@Repository
public interface PatientLifeStyleRepository extends MongoRepository<PatientLifeStyleCollection, ObjectId> {
	@Query("{'patientId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}")
	PatientLifeStyleCollection findByUserIdDoctorIdLocationIdAndHospitalId(ObjectId patientId, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);

	@Query("{'assessmentId': ?0}")
	PatientLifeStyleCollection findByassessmentId(ObjectId assessmentId);
}
