package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.GrowthAssessmentAndGeneralBioMetricsCollection;

public interface GrowthAssessmentAndGeneralBioMetricsRepository extends MongoRepository<GrowthAssessmentAndGeneralBioMetricsCollection, ObjectId>{

}
