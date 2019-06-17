package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.WeightCounterSettingCollection;

public interface WeightCounterSettingRepository extends MongoRepository<WeightCounterSettingCollection, ObjectId> {

	@Query("{'userId' : ?0}")
	public WeightCounterSettingCollection findByuserId(ObjectId userId);

}
