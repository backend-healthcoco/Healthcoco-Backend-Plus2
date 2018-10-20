package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CaloriesCounterCollection;

public interface CaloriesCounterRepository extends MongoRepository<CaloriesCounterCollection, ObjectId> {

	@Query("{'userId' : ?0, 'date' : {$gte : ?1 ,$lte : ?2},'discarded' : false }")
	public CaloriesCounterCollection findByuserId(ObjectId userId, DateTime fromDate, DateTime toDate);

}
