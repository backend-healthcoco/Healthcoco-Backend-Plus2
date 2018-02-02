package com.dpdocter.repository;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DentalLabPickupCollection;

public interface DentalLabTestPickupRepository extends MongoRepository<DentalLabPickupCollection, ObjectId>{

	@Query(value = "{'dentalLabId':?0, 'updatedTime' : {'$gt' : ?1, '$lte' : ?2}}", count = true)
	Integer findTodaysCompletedReport(ObjectId locationId, Date start, Date end);
}
