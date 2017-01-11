package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PSCollection;

public interface PSRepository extends MongoRepository<PSCollection, ObjectId> {

}
