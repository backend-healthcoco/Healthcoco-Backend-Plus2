package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorPatientLedgerCollection;

public interface DoctorPatientLedgerRepository extends MongoRepository<DoctorPatientLedgerCollection, ObjectId> {

	DoctorPatientLedgerCollection findByInvoiceId(ObjectId id);

	DoctorPatientLedgerCollection findByReceiptId(ObjectId id);

}
