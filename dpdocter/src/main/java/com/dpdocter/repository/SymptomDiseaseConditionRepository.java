package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SymptomDiseaseConditionCollection;

public interface SymptomDiseaseConditionRepository extends MongoRepository<SymptomDiseaseConditionCollection, ObjectId> {
  
}
