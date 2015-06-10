package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientClinicalNotesCollection;

@Repository
public interface PatientClinicalNotesRepository extends MongoRepository<PatientClinicalNotesCollection, String> {
	List<PatientClinicalNotesCollection> findByClinicalNotesId(String clinicalNotesId);

	@Query("{'patientId': ?0")
	List<PatientClinicalNotesCollection> findByPatientId(String patientId, Sort sort);

	@Query("{'patientId': ?0, 'createdTime': {'$gte': ?1}}")
	List<PatientClinicalNotesCollection> findByPatientId(String patientId, Date date, Sort sort);

}
