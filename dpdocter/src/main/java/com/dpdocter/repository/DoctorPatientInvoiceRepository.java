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

	DoctorPatientInvoiceCollection findByUniqueInvoiceIdAndLocationIdAndHospitalId(String uniqueInvoiceId, ObjectId locationObjectId, ObjectId hospitalObjectId);
	
	DoctorPatientInvoiceCollection findByUniqueInvoiceIdAndDoctorIdAndLocationIdAndHospitalId(String uniqueInvoiceId, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);

	DoctorPatientInvoiceCollection findByPatientIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId objectId,
			ObjectId objectId2, ObjectId objectId3, ObjectId objectId4);

	DoctorPatientInvoiceCollection findByIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId id, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);

}
