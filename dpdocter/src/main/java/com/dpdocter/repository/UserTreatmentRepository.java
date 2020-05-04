package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserTreatmentCollection;

public interface UserTreatmentRepository extends MongoRepository<UserTreatmentCollection, ObjectId> {

}
