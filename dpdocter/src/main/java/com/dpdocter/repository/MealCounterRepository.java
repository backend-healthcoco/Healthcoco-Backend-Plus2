package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.MealCounterCollection;

public interface MealCounterRepository extends MongoRepository<MealCounterCollection, ObjectId> {
	@Query("{'userId' : ?0, 'date' : {$gte : ?1 ,$lte : ?2},'mealTime': ?3,'discarded' : false }")
	public MealCounterCollection findByuserId(ObjectId userId, DateTime fromDate, DateTime toDate, String mealTime);

}
