package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorPatientDueAmountCollection;

public interface DoctorPatientDueAmountRepository extends MongoRepository<DoctorPatientDueAmountCollection, ObjectId> {

	@Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3}")
	DoctorPatientDueAmountCollection find(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

}
