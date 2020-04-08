package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VendorExpenseCollection;

public interface VendorExpenseRepository extends MongoRepository<VendorExpenseCollection, ObjectId> {

}
