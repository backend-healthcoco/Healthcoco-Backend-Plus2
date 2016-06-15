package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.PatientTreatmentCollection;

public interface PatientTreamentRepository extends MongoRepository<PatientTreatmentCollection, String> {

    @Query("{'treatmentId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3}")
    public PatientTreatmentCollection findOne(String treatmentId, String locationId, String hospitalId, String doctorId);

    @Query("{'patientId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
    public List<PatientTreatmentCollection> findAll(String patientId, boolean[] discards, Date date, Pageable pageRequest);

    @Query("{'patientId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
    public List<PatientTreatmentCollection> findAll(String patientId, boolean[] discards, Date date, Sort sort);

    @Query("{'patientId' : ?0, 'locationid' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3, 'discarded': {$in: ?4}, 'updatedTime': {'$gt': ?5}}")
    public List<PatientTreatmentCollection> findAll(String patientId, String locationId, String hospitalId, String doctorId, boolean[] discards, Date date,
	    Pageable pageRequest);

    @Query("{'patientId' : ?0, 'locationid' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3, 'discarded': {$in: ?4}, 'updatedTime': {'$gt': ?5}}")
    public List<PatientTreatmentCollection> findAll(String patientId, String locationId, String hospitalId, String doctorId, boolean[] discards, Date date,
	    Sort sort);
}
