package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnCareContextCollection;

public interface OnCareContextRepository extends MongoRepository<OnCareContextCollection, ObjectId>{

}
