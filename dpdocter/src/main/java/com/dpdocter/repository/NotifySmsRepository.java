package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnNotifySmsCollection;

public interface NotifySmsRepository extends MongoRepository<OnNotifySmsCollection,ObjectId>{

	OnNotifySmsCollection findByRespRequestId(String requestId);

}
