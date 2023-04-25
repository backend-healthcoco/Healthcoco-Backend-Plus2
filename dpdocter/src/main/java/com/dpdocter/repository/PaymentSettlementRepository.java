package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PaymentSettlementCollection;

public interface PaymentSettlementRepository extends MongoRepository<PaymentSettlementCollection, ObjectId> {

}
