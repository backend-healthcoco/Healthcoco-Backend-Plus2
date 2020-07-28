package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorSubscriptionPaymentCollection;

public interface DoctorSubscriptionPaymentRepository extends MongoRepository<DoctorSubscriptionPaymentCollection, ObjectId> {

	@Query(value = "{'doctorId':?0 }", count = true)
	public int countByDoctorId(ObjectId doctorId);
}
