package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorPatientInvoiceCollection;

public interface DoctorPatientInvoiceRepository extends MongoRepository<DoctorPatientInvoiceCollection, ObjectId>, PagingAndSortingRepository<DoctorPatientInvoiceCollection, ObjectId> {

}
