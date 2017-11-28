package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabTestSampleCollection;

public interface LabTestSampleRepository extends MongoRepository<LabTestSampleCollection, ObjectId> {
	@Query("{'_id': {$in : ?0}}")
	List<LabTestSampleCollection> findbyIds(List<ObjectId> ids);
}
