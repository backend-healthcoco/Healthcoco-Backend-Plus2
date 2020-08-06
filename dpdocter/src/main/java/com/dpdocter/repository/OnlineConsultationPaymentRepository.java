package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.beans.OnlineConsultionPaymentCollection;

public interface OnlineConsultationPaymentRepository extends MongoRepository<OnlineConsultionPaymentCollection, ObjectId>{

}
