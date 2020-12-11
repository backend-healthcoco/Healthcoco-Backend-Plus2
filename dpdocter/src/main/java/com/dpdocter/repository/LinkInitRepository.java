package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.beans.LinkInitCollection;

public interface LinkInitRepository extends MongoRepository<LinkInitCollection, ObjectId>{

	LinkInitCollection findByRequestId(String requestId);

}
