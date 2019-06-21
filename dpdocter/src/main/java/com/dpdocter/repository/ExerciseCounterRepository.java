package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ExerciseCounterCollection;

public interface ExerciseCounterRepository extends MongoRepository<ExerciseCounterCollection, ObjectId> {
	@Query("{'userId' : ?0, 'date' : {$gte : ?1 ,$lte : ?2},'discarded' : false }")
	public ExerciseCounterCollection findByuserId(ObjectId userId, DateTime fromDate, DateTime toDate);

}
