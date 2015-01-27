package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientClinicalNotesCollection;

@Repository
public interface PatientClinicalNotesRepository extends MongoRepository<PatientClinicalNotesCollection, String>{
	List<PatientClinicalNotesCollection> findByPatientId(String patientId);
}
