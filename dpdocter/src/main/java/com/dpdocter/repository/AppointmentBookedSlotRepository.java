package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AppointmentBookedSlotCollection;

public interface AppointmentBookedSlotRepository extends MongoRepository<AppointmentBookedSlotCollection, String>{

	@Query("{'userLocationId': ?0, 'date': ?1}")
	List<AppointmentBookedSlotCollection> findByUserLocationId(String userLocationId, Date date);

	@Query("{'appointmentId': ?0}")
	AppointmentBookedSlotCollection findByAppointmentId(String appointmentId);

}
