package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorPatientLedgerCollection;

public interface DoctorPatientLedgerRepository extends MongoRepository<DoctorPatientLedgerCollection, ObjectId> {

	@Query("{'invoiceId': ?0}")
	DoctorPatientLedgerCollection findByInvoiceId(ObjectId id);

	@Query("{'receiptId': ?0}")
	DoctorPatientLedgerCollection findByReceiptId(ObjectId id);

}
