package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.HiuDataRequestCollection;

public interface HiuDataRequestRepository extends MongoRepository<HiuDataRequestCollection,ObjectId>{

	HiuDataRequestCollection findByRespRequestId(String requestId);

	HiuDataRequestCollection findByHiRequestTransactionId(String transactionId);

}
