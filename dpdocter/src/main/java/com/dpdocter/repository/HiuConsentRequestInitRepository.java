package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ConsentOnInitRequestCollection;

public interface HiuConsentRequestInitRepository extends MongoRepository<ConsentOnInitRequestCollection,ObjectId>{

}
