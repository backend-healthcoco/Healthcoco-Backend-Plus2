package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MasterBabyAchievementCollection;

public interface MasterBabyAchievementRepository extends MongoRepository<MasterBabyAchievementCollection, ObjectId>{

}
