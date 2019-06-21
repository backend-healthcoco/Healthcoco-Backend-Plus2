package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PrescriptionFeedbackCollection;

public interface PrescritptionFeedbackRepository extends MongoRepository<PrescriptionFeedbackCollection, ObjectId> {

}
