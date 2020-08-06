package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SMSDeliveryReportsCollection;

public interface SmsDeliveryReportsRepository extends MongoRepository<SMSDeliveryReportsCollection, ObjectId>{

}
