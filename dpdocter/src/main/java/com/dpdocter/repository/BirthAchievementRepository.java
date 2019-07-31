package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BirthAchievementCollection;

public interface BirthAchievementRepository extends MongoRepository<BirthAchievementCollection, ObjectId>{

	 public List<BirthAchievementCollection> findByPatientId(ObjectId patientId);
	
}
