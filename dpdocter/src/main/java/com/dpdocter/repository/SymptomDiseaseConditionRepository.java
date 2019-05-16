package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SymptomDiseaseConditionCollection;

public interface SymptomDiseaseConditionRepository extends MongoRepository<SymptomDiseaseConditionCollection, ObjectId> {

//	@Query("{'service' : {$in : ?0}}")
//	List<ServicesCollection> findbyService(List<String> services);
//
//	@Query("{'specialityIds' : {$in : ?0}}")
//	List<ServicesCollection> findbySpeciality(List<ObjectId> oldSpecialities);

  
}
