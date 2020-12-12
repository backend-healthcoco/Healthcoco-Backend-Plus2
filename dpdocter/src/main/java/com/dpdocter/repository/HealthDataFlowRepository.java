package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.HipDataFlowCollection;

public interface HealthDataFlowRepository extends MongoRepository<HipDataFlowCollection, ObjectId>{

	HipDataFlowCollection findByTransactionId(String transactionId);

}
