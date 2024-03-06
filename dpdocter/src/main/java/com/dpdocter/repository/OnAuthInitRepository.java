package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnAuthInitCollection;

public interface OnAuthInitRepository extends MongoRepository<OnAuthInitCollection, ObjectId>{

	OnAuthInitCollection findByRespRequestId(String requestId);

}
