package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AppointmentGeneralFeedbackCollection;

public interface AppointmentGeneralFeedbackRepository extends MongoRepository<AppointmentGeneralFeedbackCollection, ObjectId> {

}
