package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.RateCardCollection;

public interface LabReportsRepository extends MongoRepository<LabReportsCollection, ObjectId>{

	
	@Query("{'labTestSampleId': ?0}")
	public LabReportsCollection getByRequestIdandSAmpleId(ObjectId labTestSampleId );
	
}
