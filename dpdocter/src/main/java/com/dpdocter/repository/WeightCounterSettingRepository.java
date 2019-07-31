package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.WeightCounterSettingCollection;

public interface WeightCounterSettingRepository extends MongoRepository<WeightCounterSettingCollection, ObjectId> {

	public WeightCounterSettingCollection findByUserId(ObjectId userId);

}
