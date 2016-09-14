package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.IPDReportsCollection;

public interface IPDReportsRepository extends MongoRepository<IPDReportsCollection, ObjectId> {

}
