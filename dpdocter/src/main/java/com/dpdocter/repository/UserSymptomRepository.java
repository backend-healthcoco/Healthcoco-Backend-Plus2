package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserSymptomCollection;

public interface UserSymptomRepository extends MongoRepository<UserSymptomCollection, ObjectId> {

}
