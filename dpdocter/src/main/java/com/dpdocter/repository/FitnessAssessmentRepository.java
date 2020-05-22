package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FitnessAssessmentCollection;

public interface FitnessAssessmentRepository extends MongoRepository<FitnessAssessmentCollection, ObjectId>{

}
