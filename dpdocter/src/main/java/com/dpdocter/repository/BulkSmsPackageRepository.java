package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BulkSmsPackageCollection;

public interface BulkSmsPackageRepository extends MongoRepository<BulkSmsPackageCollection, ObjectId>{

}
