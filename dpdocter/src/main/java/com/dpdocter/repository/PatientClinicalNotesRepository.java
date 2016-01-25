package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientClinicalNotesCollection;

@Repository
public interface PatientClinicalNotesRepository extends MongoRepository<PatientClinicalNotesCollection, String>,
	PagingAndSortingRepository<PatientClinicalNotesCollection, String> {

    @Query("{'clinicalNotesId': ?0}")
    List<PatientClinicalNotesCollection> findByClinicalNotesId(String clinicalNotesId);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, Date date, Pageable Pageable);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}}")
    List<PatientClinicalNotesCollection> findByPatientId(String patientId, Date date, Sort sort);

    @Query("{'patientId': ?0, 'clinicalNotesId': {'$in': ?1}}")
    List<PatientClinicalNotesCollection> findAll(String patientId, List<String> clinicalNotesIds);

    @Query(value = "{'patientId': ?0, 'clinicalNotesId': {'$in': ?1}}", count = true)
    Integer findCount(String patientId, List<String> clinicalNotesIds);

    @Query(value = "{'patientId': ?0, 'clinicalNotesId': ?1}")
	PatientClinicalNotesCollection findByPatientIdClinicalNotesId(String patientId, String id);

    @Query("{'patientId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
	List<PatientClinicalNotesCollection> findByPatientId(String patientId, boolean[] discards, Date date, Pageable pageRequest);

    @Query("{'patientId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
	List<PatientClinicalNotesCollection> findByPatientId(String patientId, boolean[] discards, Date date, Sort sort);

    @Query(value = "{'patientId': ?0}", count = true)
	Integer findCount(String patientId);

}
