package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorPatientInvoiceCollection;

public interface DoctorPatientInvoiceRepository extends MongoRepository<DoctorPatientInvoiceCollection, ObjectId>,
		PagingAndSortingRepository<DoctorPatientInvoiceCollection, ObjectId> {
	
	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query(value = "{'patientId' : ?0}", count = true)
	Integer countByPatientId(ObjectId patientId);
}
