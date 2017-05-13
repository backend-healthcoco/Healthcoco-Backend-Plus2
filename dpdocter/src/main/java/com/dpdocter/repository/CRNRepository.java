package com.dpdocter.repository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CRNCollection;

public interface CRNRepository extends MongoRepository<CRNCollection, ObjectId>{

	@Query("{ 'locationId': ?0, 'crnNumber': ?1 , 'requestId' : ?2}")
	CRNCollection getbylocationIdandCRN(ObjectId locationId, String crn , String requestId);
	
}
