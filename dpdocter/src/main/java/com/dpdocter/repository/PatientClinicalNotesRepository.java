package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientClinicalNotesCollection;

@Repository
public interface PatientClinicalNotesRepository extends MongoRepository<PatientClinicalNotesCollection, String>, PagingAndSortingRepository<PatientClinicalNotesCollection, String> {
    List<PatientClinicalNotesCollection> findByClinicalNotesId(String clinicalNotesId);

    @Query("{'patientId': ?0}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, Sort sort, PageRequest pageRequest);

    @Query("{'patientId': ?0, 'createdTime': {'$gte': ?1}}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'patientId': ?0, 'isDeleted': ?1, 'createdTime': {'$gte': ?2}}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, boolean isDeleted, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'patientId': ?0, 'isDeleted': ?1}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, boolean isDeleted, Sort sort, PageRequest pageRequest);

    @Query("{'patientId': ?0, 'clinicalNotesId': {'$in': ?1}}")
    List<PatientClinicalNotesCollection> findAll(String patientId, List<String> clinicalNotesIds);

    @Query(value = "{'patientId': ?0, 'clinicalNotesId': {'$in': ?1}}", count = true)
    Integer findCount(String patientId, List<String> clinicalNotesIds);

}
