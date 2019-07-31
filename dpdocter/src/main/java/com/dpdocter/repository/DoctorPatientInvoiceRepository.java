package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorPatientInvoiceCollection;

public interface DoctorPatientInvoiceRepository extends MongoRepository<DoctorPatientInvoiceCollection, ObjectId>,
		PagingAndSortingRepository<DoctorPatientInvoiceCollection, ObjectId> {
	
	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3,'discarded' : false, 'isPatientDiscarded': {'$ne' : true}}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query(value = "{'patientId' : ?0,'discarded' : false, 'isPatientDiscarded':{ '$ne' : true } }", count = true)
	Integer countByPatientId(ObjectId patientId);

	@Query("{'uniqueInvoiceId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2}")
	DoctorPatientInvoiceCollection findByUniqueInvoiceIdAndLocationIdAndHospitalId(String uniqueInvoiceId, ObjectId locationObjectId, ObjectId hospitalObjectId);
	
	DoctorPatientInvoiceCollection find(String uniqueInvoiceId, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);

}
