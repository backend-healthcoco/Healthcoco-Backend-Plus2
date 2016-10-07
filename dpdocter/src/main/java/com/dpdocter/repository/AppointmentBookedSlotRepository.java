package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AppointmentBookedSlotCollection;

public interface AppointmentBookedSlotRepository extends MongoRepository<AppointmentBookedSlotCollection, ObjectId> {

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'fromDate': {'$gte':?2}},{'doctorId': ?0, 'locationId': ?1, 'toDate': {'$lte':?2}}]}")
    List<AppointmentBookedSlotCollection> findByDoctorLocationId(ObjectId doctorId, ObjectId locationId, Date date );

    @Query("{'appointmentId': ?0}")
    AppointmentBookedSlotCollection findByAppointmentId(String appointmentId);

}
