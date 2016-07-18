package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.PatientTreatmentCollection;

public interface PatientTreamentRepository extends MongoRepository<PatientTreatmentCollection, ObjectId> {

    @Query("{'treatmentId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3}")
    public PatientTreatmentCollection findOne(ObjectId treatmentId, ObjectId locationId, ObjectId hospitalId, ObjectId doctorId);

    @Query("{'patientId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
    public List<PatientTreatmentCollection> findAll(ObjectId patientId, boolean[] discards, Date date, Pageable pageRequest);

    @Query("{'patientId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
    public List<PatientTreatmentCollection> findAll(ObjectId patientId, boolean[] discards, Date date, Sort sort);

    @Query("{'patientId' : ?0, 'locationid' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3, 'discarded': {$in: ?4}, 'updatedTime': {'$gt': ?5}}")
    public List<PatientTreatmentCollection> findAll(ObjectId patientId, ObjectId locationId, ObjectId hospitalId, ObjectId doctorId, boolean[] discards, Date date,
	    Pageable pageRequest);

    @Query("{'patientId' : ?0, 'locationid' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3, 'discarded': {$in: ?4}, 'updatedTime': {'$gt': ?5}}")
    public List<PatientTreatmentCollection> findAll(ObjectId patientId, ObjectId locationId, ObjectId hospitalId, ObjectId doctorId, boolean[] discards, Date date,
	    Sort sort);
}
