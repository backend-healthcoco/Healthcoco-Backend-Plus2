package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPDReportsRepository extends MongoRepository<IPDReportsRepository, ObjectId> {

}
