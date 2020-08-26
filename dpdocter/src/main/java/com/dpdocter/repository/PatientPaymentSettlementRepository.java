package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PatientPaymentDetailsCollection;

public interface PatientPaymentSettlementRepository extends MongoRepository<PatientPaymentDetailsCollection,ObjectId>{

	PatientPaymentDetailsCollection findByOrderId(String order_id);

}
