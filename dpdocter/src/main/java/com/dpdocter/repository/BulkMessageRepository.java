package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BulKMessageCollection;

public interface BulkMessageRepository extends MongoRepository<BulKMessageCollection, ObjectId> {

}
