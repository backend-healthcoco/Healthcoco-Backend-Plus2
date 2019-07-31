package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DailyImprovementFeedbackCollection;

public interface DailyImprovementFeedbackRepository extends MongoRepository<DailyImprovementFeedbackCollection, ObjectId>{

	 DailyImprovementFeedbackCollection findByPrescriptionId(ObjectId prescriptionId);
	
}
