package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NdhmNotifyCollection;

public interface NdhmNotifyRepository extends MongoRepository<NdhmNotifyCollection,ObjectId>{

	NdhmNotifyCollection findByRequestId(String requestId);

	NdhmNotifyCollection findByNotificationConsentId(String id);

}
