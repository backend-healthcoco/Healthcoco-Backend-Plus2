package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientMeasurementCollection;

@Repository
public interface PatientMeasurementRepository extends MongoRepository<PatientMeasurementCollection, ObjectId> {

	@Query("{'patientId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}")
	PatientMeasurementCollection findByUserIdDoctorIdLocationIdAndHospitalId(ObjectId patientId, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);

	@Query("{'assessmentId': ?0}")
	PatientMeasurementCollection findByassessmentId(ObjectId assessmentId);
}
