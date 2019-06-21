package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.BirthAchievementCollection;

public interface BirthAchievementRepository extends MongoRepository<BirthAchievementCollection, ObjectId>{

	@Query("{'patientId': ?0}")
	 public List<BirthAchievementCollection> findBypatientId(ObjectId patientId);
	
}
