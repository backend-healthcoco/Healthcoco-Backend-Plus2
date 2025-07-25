package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.WaterCounterSettingCollection;

public interface WaterCounterSettingRepository extends MongoRepository<WaterCounterSettingCollection, ObjectId>{

	//@Query("{'userId' : ?0,'discarded' : false }")
	public WaterCounterSettingCollection findByUserId(ObjectId userId);

}
