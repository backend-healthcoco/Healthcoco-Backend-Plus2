package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabTestPickupCollection;

public interface LabTestPickupRepository extends MongoRepository<LabTestPickupCollection, ObjectId>{

	@Query("{'requestId' : ?0}")
	LabTestPickupCollection getByRequestId(String requestId);
	
	@Query("{'parentLabLocationId' : ?0 , 'daughterLabLocationId': ?1}")
	List<LabTestPickupCollection> getByParentDaughterLab(ObjectId parentLabLocationId,ObjectId daughterLabLocationId);
	
	@Query("{'labTestSampleIds' : ?0}")
	LabTestPickupCollection getByLabTestSampleId(ObjectId labTestSampleId);
	
}
