package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MealCounterCollection;

public interface MealCounterRepository extends MongoRepository<MealCounterCollection, ObjectId> {
	
	public MealCounterCollection findByUserIdDateBetweenAndMealTimeAndDiscardedIsFalse(ObjectId userId, DateTime fromDate, DateTime toDate, String mealTime);

}
