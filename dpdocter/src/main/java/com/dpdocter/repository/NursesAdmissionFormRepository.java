package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NursesAdmissionCollection;

public interface NursesAdmissionFormRepository extends MongoRepository<NursesAdmissionCollection, ObjectId> {

}
