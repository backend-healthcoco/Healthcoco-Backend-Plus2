package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.HistoryCollection;

public interface HistoryRepository
		extends MongoRepository<HistoryCollection, ObjectId>, PagingAndSortingRepository<HistoryCollection, ObjectId> {

//    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
//    HistoryCollection findHistory(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'patientId': ?2}")
	HistoryCollection findHistory(ObjectId locationId, ObjectId hospitalId, ObjectId patientId);

	@Query(value = "{'patientId' : ?0, 'doctorId': {$ne : ?1}, 'locationId' : {$ne : ?2}, 'hospitalId' : {$ne : ?3}}", count = true)
	Integer getByPatientIdAndNotEqualToDoctorLocationHospital(ObjectId patientId, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);

	@Query("{'patientId' : ?0}")
	List<HistoryCollection> findHistory(ObjectId patientId);

	@Query("{'patientId' : ?0, 'drugsAndAllergies':{$ne:null}}")
	List<HistoryCollection> findDrugAllergiesHistory(ObjectId objectId);

	@Query("{'assessmentId' : ?0}")
	HistoryCollection findByAssessmentId(ObjectId assessmentId);
}
