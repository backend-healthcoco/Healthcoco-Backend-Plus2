package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SearchRequestToPharmacyCollection;

public interface SearchRequestToPharmacyRepository extends MongoRepository<SearchRequestToPharmacyCollection, ObjectId> {

	@Query("{'uniqueRequestId' : ?0 , 'localeId' : ?1 , 'userId' : ?2}")
	public SearchRequestToPharmacyCollection findByRequestIdandPharmacyId(String uniqueRequestId , ObjectId localeId , ObjectId userId);
	
	@Query(value = "{'uniqueRequestId' : ?0 ,'replyType' : ?1}" , count = true)
	public Integer getCountByUniqueRequestId(String uniqueRequestId , String replyType);
	
}
