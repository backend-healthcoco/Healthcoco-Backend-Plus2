package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientFoodAndExcerciseCollection;

@Repository
public interface PatientFoodAndExerciseRepository extends MongoRepository<PatientFoodAndExcerciseCollection, ObjectId> {

	PatientFoodAndExcerciseCollection findByAssessmentId(ObjectId assessmentId);
}
