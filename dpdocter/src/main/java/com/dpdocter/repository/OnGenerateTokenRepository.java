package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnGenerateTokenCollection;

public interface OnGenerateTokenRepository extends MongoRepository<OnGenerateTokenCollection, ObjectId> {

}
