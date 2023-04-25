package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnlineConsultionPaymentCollection;


public interface OnlineConsultationPaymentRepository extends MongoRepository<OnlineConsultionPaymentCollection, ObjectId>{


	OnlineConsultionPaymentCollection findByOrderId(String order_id);


}
