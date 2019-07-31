package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.WaterCounterCollection;

public interface WaterCounterRepository extends MongoRepository<WaterCounterCollection, ObjectId> {
	//@Query("{'userId' : ?0, 'date' : {$gte : ?1 ,$lte : ?2},'discarded' : false }")
	public WaterCounterCollection findByUserIdAndDateGreaterThanAndDiscardedIsFalse(ObjectId userId, DateTime fromDate, DateTime toDate);
}
