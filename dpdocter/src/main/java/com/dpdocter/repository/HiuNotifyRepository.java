package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.HiuNotifyCollection;


public interface HiuNotifyRepository extends MongoRepository<HiuNotifyCollection, ObjectId>{

	HiuNotifyCollection findByRespRequestId(String requestId);

}
