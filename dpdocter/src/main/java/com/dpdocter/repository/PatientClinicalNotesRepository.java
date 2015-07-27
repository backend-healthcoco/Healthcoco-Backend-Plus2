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

    List<PatientClinicalNotesCollection> findByPatientId(String patientId, Sort sort);

    @Query("{'patientId': ?0, 'createdTime': {'$gte': ?1}}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, Date date, Sort sort);

    @Query("{'patientId': ?0, 'clinicalNotesId': {'$in': ?1}}")
    List<PatientClinicalNotesCollection> findAll(String patientId, List<String> clinicalNotesIds);

    @Query(value = "{'patientId': ?0, 'clinicalNotesId': {'$in': ?1}}", count = true)
    Integer findCount(String patientId, List<String> clinicalNotesIds);

}
