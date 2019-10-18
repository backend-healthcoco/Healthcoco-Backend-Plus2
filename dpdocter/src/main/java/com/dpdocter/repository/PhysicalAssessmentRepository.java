package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PhysicalAssessmentCollection;

public interface PhysicalAssessmentRepository extends MongoRepository<PhysicalAssessmentCollection, ObjectId>{

}
