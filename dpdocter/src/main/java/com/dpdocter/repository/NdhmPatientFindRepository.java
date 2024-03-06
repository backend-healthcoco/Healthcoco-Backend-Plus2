package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NdhmPatientFindCollection;

public interface NdhmPatientFindRepository extends MongoRepository<NdhmPatientFindCollection,ObjectId>{

	NdhmPatientFindCollection findByRespRequestId(String requestId);

}
