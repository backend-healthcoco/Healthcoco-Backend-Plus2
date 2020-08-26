package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SettlementCollection;

public interface SettlementRepository extends MongoRepository<SettlementCollection, ObjectId> {

}
