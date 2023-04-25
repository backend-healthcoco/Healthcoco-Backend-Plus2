package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.WaterCounterCollection;

public interface WaterCounterRepository extends MongoRepository<WaterCounterCollection, ObjectId> {
	public WaterCounterCollection findByUserIdAndDateGreaterThanAndDiscardedIsFalse(ObjectId userId, DateTime fromDate,
			DateTime toDate);
}
