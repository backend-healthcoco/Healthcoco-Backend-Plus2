package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.WaterCounterSettingCollection;

public interface WaterCounterSettingRepository extends MongoRepository<WaterCounterSettingCollection, ObjectId>{
	@Query("{'userId' : ?0}")
	public WaterCounterSettingCollection findByuserId(ObjectId userId);

}
