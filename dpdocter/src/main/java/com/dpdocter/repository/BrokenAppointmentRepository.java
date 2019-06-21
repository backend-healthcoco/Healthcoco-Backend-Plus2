package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.BroakenAppointmentCollection;

public interface BrokenAppointmentRepository extends MongoRepository<BroakenAppointmentCollection, ObjectId>,
		PagingAndSortingRepository<BroakenAppointmentCollection, ObjectId> {

}
