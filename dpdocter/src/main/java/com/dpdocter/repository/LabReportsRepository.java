package com.dpdocter.repository;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabReportsCollection;

public interface LabReportsRepository extends MongoRepository<LabReportsCollection, ObjectId> {

	public LabReportsCollection findByLabTestSampleId(ObjectId labTestSampleId);

	@Query(value = "{'parentLabLocationId':?0, 'isCompleted':?1, 'updatedTime' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
	Integer findTodaysCompletedReport(ObjectId locationId, Boolean isCompleted, Date start, Date end);
}
