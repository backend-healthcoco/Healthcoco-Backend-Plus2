package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientTreatmentCollection;
@Repository
public interface PatientTreamentRepository extends MongoRepository<PatientTreatmentCollection, ObjectId> {

	@Query("{'id' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3}")
	public PatientTreatmentCollection findOne(ObjectId treatmentId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);
	@Query("{'id' : {$in:?0}}")
	public List<PatientTreatmentCollection> findByIds(List<ObjectId> id);
	
	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query(value = "{'patientId' : ?0}", count = true)
	Integer countByPatientId(ObjectId patientId);
}
