package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorPatientReceiptCollection;

public interface DoctorPatientReceiptRepository extends MongoRepository<DoctorPatientReceiptCollection, ObjectId>, PagingAndSortingRepository<DoctorPatientReceiptCollection, ObjectId>{

}
