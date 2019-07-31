package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SearchRequestToPharmacyCollection;

@Repository
public interface SearchRequestToPharmacyRepository
		extends MongoRepository<SearchRequestToPharmacyCollection, ObjectId> {

	public SearchRequestToPharmacyCollection findByUniqueRequestIdAndLocaleIdAndUserId(String uniqueRequestId, ObjectId localeId,
			ObjectId userId);

	@Query(value = "{'uniqueRequestId' : ?0 ,'replyType' : ?1}", count = true)
	public Integer getCountByUniqueRequestId(String uniqueRequestId, String replyType);

	public List<SearchRequestToPharmacyCollection> findByUniqueRequestIdAndReplyTypeAndLocaleId(String uniqueRequestId, String replyType, ObjectId localeId);

}
