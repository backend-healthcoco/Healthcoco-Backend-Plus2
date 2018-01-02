package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.AdmitCardCollection;

@Repository
public interface AdmitCardRepository extends MongoRepository<AdmitCardCollection, ObjectId>,
		PagingAndSortingRepository<AdmitCardCollection, ObjectId> {
	
	@Query(value = "{'patientId' : ?0,'discarded' : false, 'isPatientDiscarded' : false}", count = true)
	Integer countByPatientId(ObjectId patientId);

	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3,'discarded' : false, 'isPatientDiscarded' : false}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

}
