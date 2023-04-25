package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserCartCollection;

public interface UserCartRepository extends MongoRepository<UserCartCollection, ObjectId> {

	UserCartCollection findByUserId(ObjectId userId);

}
