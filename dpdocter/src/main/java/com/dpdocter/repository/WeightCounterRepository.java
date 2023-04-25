package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.WeightCounterCollection;

public interface WeightCounterRepository extends MongoRepository<WeightCounterCollection, ObjectId> {

	public WeightCounterCollection findByUserIdAndDateGreaterThanAndDiscardedIsFalse(ObjectId userId, DateTime fromDate,
			DateTime toDate);

}
