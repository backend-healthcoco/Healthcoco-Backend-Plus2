package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BankDetailsCollection;

public interface BankDetailsRepository extends MongoRepository<BankDetailsCollection,ObjectId>{

}
