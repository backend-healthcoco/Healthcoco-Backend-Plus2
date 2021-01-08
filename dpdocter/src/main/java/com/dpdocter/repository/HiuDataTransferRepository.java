package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.HiuDataTransferCollection;

public interface HiuDataTransferRepository extends MongoRepository<HiuDataTransferCollection, ObjectId>{

	HiuDataTransferCollection findByTransactionId(String transactionId);

}
