package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NutritionGoalStatusStampingCollection;

public interface NutritionGoalStatusStampingRepository extends MongoRepository<NutritionGoalStatusStampingCollection, ObjectId> {

	NutritionGoalStatusStampingCollection findByPatientIdAndDoctorIdAndLocationIdAndHospitalIdAndGoalStatus(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String status);
	
}
