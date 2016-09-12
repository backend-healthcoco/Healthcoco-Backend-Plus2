package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OPDReportsCollection;

public interface OPDReportsRepository extends MongoRepository<OPDReportsCollection, ObjectId> {

}
