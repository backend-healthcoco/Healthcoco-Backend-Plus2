package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorProfileViewCollection;

public interface DoctorProfileViewRepository extends MongoRepository<DoctorProfileViewCollection, ObjectId> {

}
