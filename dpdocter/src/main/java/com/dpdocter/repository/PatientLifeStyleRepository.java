package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientLifeStyleCollection;

@Repository
public interface PatientLifeStyleRepository extends MongoRepository<PatientLifeStyleCollection, ObjectId> {
	
	PatientLifeStyleCollection findByAssessmentId(ObjectId assessmentId);

	PatientLifeStyleCollection findByPatientId(ObjectId patientid);
}
