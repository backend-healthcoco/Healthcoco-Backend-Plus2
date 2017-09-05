package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DailyImprovementFeedbackCollection;

public interface DailyImprovementFeedbackRepository extends MongoRepository<DailyImprovementFeedbackCollection, ObjectId>{

	 @Query("{'prescriptionId': ?0}")
	 DailyImprovementFeedbackCollection findByPrescriptionId(ObjectId prescriptionId);
	
}
