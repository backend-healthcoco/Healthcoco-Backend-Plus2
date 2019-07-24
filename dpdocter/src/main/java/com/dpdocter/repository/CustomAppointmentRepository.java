package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CustomAppointmentCollection;

public interface CustomAppointmentRepository extends MongoRepository<CustomAppointmentCollection, ObjectId> {

	@Query("{'id' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3}")
	public CustomAppointmentCollection findById(ObjectId treatmentId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);
}
