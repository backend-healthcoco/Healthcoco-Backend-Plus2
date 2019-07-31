package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientMeasurementCollection;

@Repository
public interface PatientMeasurementRepository extends MongoRepository<PatientMeasurementCollection, ObjectId> {

	PatientMeasurementCollection findByAssessmentId(ObjectId assessmentId);
}
