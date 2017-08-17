package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PatientFeedbackCollection;

public interface PatientFeedbackRepository extends MongoRepository<PatientFeedbackCollection, ObjectId>{

}
