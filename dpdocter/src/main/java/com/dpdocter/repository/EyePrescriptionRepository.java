package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.EyePrescriptionCollection;

public interface EyePrescriptionRepository extends MongoRepository<EyePrescriptionCollection, ObjectId>{

	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3,'discarded' : false}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query(value = "{'patientId' : ?0,'discarded' : false}", count = true)
	Integer countByPatientId(ObjectId patientId);
	
}
