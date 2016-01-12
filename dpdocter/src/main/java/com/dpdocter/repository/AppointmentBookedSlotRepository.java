package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AppointmentBookedSlotCollection;

public interface AppointmentBookedSlotRepository extends MongoRepository<AppointmentBookedSlotCollection, String>{

	@Query("{'doctorId': ?0, 'locationId': ?1, 'date': ?2}")
	List<AppointmentBookedSlotCollection> findByDoctorLocationId(String doctorId, String locationId, Date date);

	@Query("{'appointmentId': ?0}")
	AppointmentBookedSlotCollection findByAppointmentId(String appointmentId);

}
