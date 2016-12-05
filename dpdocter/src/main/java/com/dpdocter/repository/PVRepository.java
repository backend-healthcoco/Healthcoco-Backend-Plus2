package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PVCollection;

public interface PVRepository extends MongoRepository<PVCollection, ObjectId> {

}
