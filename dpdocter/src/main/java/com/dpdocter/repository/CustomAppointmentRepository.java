package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CustomAppointmentCollection;

public interface CustomAppointmentRepository extends MongoRepository<CustomAppointmentCollection, ObjectId> {

	public CustomAppointmentCollection findByIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId treatmentId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);
}
