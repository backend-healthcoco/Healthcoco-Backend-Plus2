package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientFoodAndExcerciseCollection;

@Repository
public interface PatientFoodAndExerciseRepository extends MongoRepository<PatientFoodAndExcerciseCollection, ObjectId> {

	@Query("{'patientId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}")
	PatientFoodAndExcerciseCollection findByUserIdDoctorIdLocationIdAndHospitalId(ObjectId patientId, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);
	
	@Query("{'assessmentId': ?0}")
	PatientFoodAndExcerciseCollection findByassessmentId(ObjectId assessmentId);
}
