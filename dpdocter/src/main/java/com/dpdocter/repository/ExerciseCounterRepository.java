package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ExerciseCounterCollection;

public interface ExerciseCounterRepository extends MongoRepository<ExerciseCounterCollection, ObjectId> {
	public ExerciseCounterCollection findByUserIdAndDateBetweenAndDiscardedIsFalse(ObjectId userId, DateTime fromDate, DateTime toDate);

}
