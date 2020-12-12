package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LinkConfirmCollection;

public interface LinkConfirmRepository extends MongoRepository<LinkConfirmCollection, ObjectId>{

	LinkConfirmCollection findByRequestId(String requestId);



}
