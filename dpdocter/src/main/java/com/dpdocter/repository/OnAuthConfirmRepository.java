package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.beans.OnAuthConfirmCollection;

public interface OnAuthConfirmRepository extends MongoRepository<OnAuthConfirmCollection, ObjectId>{

	OnAuthConfirmCollection findByRespRequestId(String requestId);

}
