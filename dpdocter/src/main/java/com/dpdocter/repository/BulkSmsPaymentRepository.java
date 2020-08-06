package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BulkSmsPaymentCollection;

public interface BulkSmsPaymentRepository extends MongoRepository<BulkSmsPaymentCollection, ObjectId> {

	String countByDoctorId(ObjectId doctorId);

}
