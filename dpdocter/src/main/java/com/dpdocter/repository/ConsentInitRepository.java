package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ConsentInitCollection;

public interface ConsentInitRepository extends MongoRepository<ConsentInitCollection, ObjectId>{

	ConsentInitCollection findByRespRequestId(String requestId);

}
