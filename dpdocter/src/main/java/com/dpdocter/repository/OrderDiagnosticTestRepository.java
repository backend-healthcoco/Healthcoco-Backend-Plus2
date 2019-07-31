package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.OrderDiagnosticTestCollection;

public interface OrderDiagnosticTestRepository extends MongoRepository<OrderDiagnosticTestCollection, ObjectId>, PagingAndSortingRepository<OrderDiagnosticTestCollection, ObjectId>{

	OrderDiagnosticTestCollection findByIdAndUserId(ObjectId id, ObjectId userId);

	@Query(value = "{'pickUpDate': {'$gt' : ?0, '$lte' : ?1}, 'pickUpTime.fromTime' : ?2}", count = true)
	Integer countByDateAndTime(long slotStartTime, long slotEndTime, long fromTime);
}
