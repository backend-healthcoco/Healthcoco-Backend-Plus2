package com.dpdocter.repository;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabTestSampleCollection;

public interface LabTestSampleRepository extends MongoRepository<LabTestSampleCollection, ObjectId> {

	@Query(value = "{'parentLabLocationId':?0, 'isCompleted':?1, 'updatedTime' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
	Integer findTodaysCompletedReport(ObjectId locationId, Boolean isCompleted, Date start, Date end);
}
