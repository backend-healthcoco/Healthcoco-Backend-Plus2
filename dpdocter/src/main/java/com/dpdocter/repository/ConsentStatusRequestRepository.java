package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnConsentRequestStatusCollection;

public interface ConsentStatusRequestRepository extends MongoRepository<OnConsentRequestStatusCollection,ObjectId>{

	OnConsentRequestStatusCollection findByRespRequestId(String requestId);

}
