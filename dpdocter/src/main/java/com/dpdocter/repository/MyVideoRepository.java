package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MyVideoCollection;

public interface MyVideoRepository extends MongoRepository<MyVideoCollection, ObjectId> {

}
