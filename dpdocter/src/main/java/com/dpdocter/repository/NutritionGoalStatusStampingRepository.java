package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.NutritionGoalStatusStampingCollection;

public interface NutritionGoalStatusStampingRepository extends MongoRepository<NutritionGoalStatusStampingCollection, ObjectId> {

	@Query("{ 'patientId' :?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'goalStatus': ?4}")
	NutritionGoalStatusStampingCollection getByPatientDoctorLocationHospitalandStatus(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String status);
	
	
}
