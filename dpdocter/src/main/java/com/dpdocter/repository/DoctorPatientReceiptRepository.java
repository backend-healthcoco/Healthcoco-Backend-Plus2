package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.beans.AdvanceReceiptIdWithAmount;
import com.dpdocter.collections.DoctorPatientReceiptCollection;

public interface DoctorPatientReceiptRepository extends MongoRepository<DoctorPatientReceiptCollection, ObjectId>,
		PagingAndSortingRepository<DoctorPatientReceiptCollection, ObjectId> {

	@Query("{'receiptType': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'patientId': ?4, 'remainingAdvanceAmount': {'$gt': 0.0}, 'discarded': false}")
	List<DoctorPatientReceiptCollection> findAvailableAdvanceReceipts(String name, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId, ObjectId patientId, Sort sort);

	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query(value = "{'patientId' : ?0}", count = true)
	Integer countByPatientId(ObjectId patientId);

	@Query("{'advanceReceiptIdWithAmounts.receiptId': ?0}")
	List<DoctorPatientReceiptCollection> findAllByAdvanceId(ObjectId advanceReceiptId);

	@Query("{'invoiceId': ?0}")
	List<DoctorPatientReceiptCollection> findByInvoiceId(ObjectId invoiceId);
}
