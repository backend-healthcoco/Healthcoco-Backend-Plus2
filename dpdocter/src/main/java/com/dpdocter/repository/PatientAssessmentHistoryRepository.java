package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PatientAssessmentHistoryCollection;

public interface PatientAssessmentHistoryRepository extends MongoRepository<PatientAssessmentHistoryCollection, ObjectId>, PagingAndSortingRepository<PatientAssessmentHistoryCollection, ObjectId> {

	PatientAssessmentHistoryCollection findByAssessmentId(ObjectId assessmentId);

}
