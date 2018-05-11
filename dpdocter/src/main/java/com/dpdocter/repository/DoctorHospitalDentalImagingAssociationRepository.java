package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorHospitalDentalImagingAssociationCollection;

public interface DoctorHospitalDentalImagingAssociationRepository extends MongoRepository<DoctorHospitalDentalImagingAssociationCollection, ObjectId>{

	
	@Query("{'doctorId': ?0 , 'hospitalId': ?1}")
	public DoctorHospitalDentalImagingAssociationCollection findbyDoctorHospital(ObjectId doctorId, ObjectId hospitalId);

}
