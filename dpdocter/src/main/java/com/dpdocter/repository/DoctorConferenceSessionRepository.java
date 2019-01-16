package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorConferenceSessionCollection;

public interface DoctorConferenceSessionRepository extends MongoRepository<DoctorConferenceSessionCollection, ObjectId> {

}
