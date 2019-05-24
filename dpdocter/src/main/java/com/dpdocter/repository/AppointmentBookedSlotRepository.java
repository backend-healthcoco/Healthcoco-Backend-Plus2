package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AppointmentBookedSlotCollection;

public interface AppointmentBookedSlotRepository extends MongoRepository<AppointmentBookedSlotCollection, ObjectId> {

	@Query("{'$or': [{'doctorId':  ?0}, {'doctorIds': ?0}], 'locationId': ?1, '$or':[{'fromDate':{'$lt': ?2}, 'toDate':{'$gte': ?2}},"
			+ "{'fromDate':{'$lte': ?3}, 'toDate':{'$gt': ?3}}],"
			+ " 'isPatientDiscarded': { '$ne' : true} }")
	List<AppointmentBookedSlotCollection> findByDoctorLocationId(ObjectId doctorId, ObjectId locationId, DateTime start,
			DateTime end, Sort sort);

	@Query("{'appointmentId': ?0}")
	AppointmentBookedSlotCollection findByAppointmentId(String appointmentId);

}
//'$or': [{'fromDate': {'$lte':?2}, 'toDate': {'$gte':?2}},"
//		+ " {'fromDate': {'$lte':?3}, 'toDate': {'$gte':?3}}]