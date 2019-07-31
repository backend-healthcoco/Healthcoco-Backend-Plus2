package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CaloriesCounterCollection;

public interface CaloriesCounterRepository extends MongoRepository<CaloriesCounterCollection, ObjectId> {

	public CaloriesCounterCollection findByUserIdAndDateBetweenAndDiscardedIsFalse(ObjectId userId, DateTime fromDate, DateTime toDate);

}
