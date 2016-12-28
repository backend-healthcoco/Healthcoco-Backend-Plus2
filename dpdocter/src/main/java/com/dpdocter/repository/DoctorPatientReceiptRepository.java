package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorPatientReceiptCollection;

public interface DoctorPatientReceiptRepository extends MongoRepository<DoctorPatientReceiptCollection, ObjectId>, PagingAndSortingRepository<DoctorPatientReceiptCollection, ObjectId>{

	@Query("{'name': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'patientId': ?3, 'remainingAdvanceAmount': {'$gt': 0.0}, 'discarded': false}")
	List<DoctorPatientReceiptCollection> findAvailableAdvanceReceipts(String name, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId, ObjectId patientId, Sort sort);

}
