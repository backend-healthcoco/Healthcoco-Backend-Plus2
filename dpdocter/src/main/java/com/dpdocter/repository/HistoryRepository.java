package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.HistoryCollection;

public interface HistoryRepository
		extends MongoRepository<HistoryCollection, ObjectId>, PagingAndSortingRepository<HistoryCollection, ObjectId> {

	List<HistoryCollection> findByLocationIdAndHospitalIdAndPatientId(ObjectId locationId, ObjectId hospitalId, ObjectId patientId);

	@Query(value = "{'patientId' : ?0, 'doctorId': {$ne : ?1}, 'locationId' : {$ne : ?2}, 'hospitalId' : {$ne : ?3}}", count = true)
	Integer getByPatientIdAndNotEqualToDoctorLocationHospital(ObjectId patientId, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);


	HistoryCollection findByAssessmentId(ObjectId assessmentId);
}
