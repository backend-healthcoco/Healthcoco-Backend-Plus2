package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AppointmentBookedSlotCollection;

public interface AppointmentBookedSlotRepository extends MongoRepository<AppointmentBookedSlotCollection, ObjectId> {

	@Query("{'doctorId': ?0, 'locationId': ?1, 'fromDate': {'$gte':?2}, 'toDate': {'$lte':?3}}")
    List<AppointmentBookedSlotCollection> findByDoctorLocationId(ObjectId doctorId, ObjectId locationId, DateTime start ,DateTime end, Sort sort);

    @Query("{'appointmentId': ?0}")
    AppointmentBookedSlotCollection findByAppointmentId(String appointmentId);

}
