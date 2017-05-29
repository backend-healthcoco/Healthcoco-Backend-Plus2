package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.enums.VisitedFor;

@Repository
public interface PatientVisitRepository extends MongoRepository<PatientVisitCollection, ObjectId>, PagingAndSortingRepository<PatientVisitCollection, ObjectId> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PatientVisitCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PatientVisitCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
    PatientVisitCollection find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId);

    @Query("{'doctorId': ?0, 'patientId' : ?1, 'visitedFor': {$in : ?2}, 'updatedTime' : {'$gt' : ?3}}")
    List<PatientVisitCollection> find(ObjectId doctorId, ObjectId patientId, List<VisitedFor> visitedFors, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'patientId' : ?1, 'visitedFor': {$in : ?2}, 'updatedTime' : {'$gt' : ?3}}")
    List<PatientVisitCollection> find(ObjectId doctorId, ObjectId patientId, List<VisitedFor> visitedFors, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3, 'visitedFor': {$in : ?4}, 'updatedTime' : {'$gt' : ?5}}")
    List<PatientVisitCollection> find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId, List<VisitedFor> visitedFors, Date date,
	    Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3, 'visitedFor': {$in : ?4}, 'updatedTime' : {'$gt' : ?5}}")
    List<PatientVisitCollection> find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId, List<VisitedFor> visitedFors, Date date,
	    Sort sort);

    @Query("{'patientId' : ?0, 'visitedFor': {$in : ?1}, 'updatedTime' : {'$gt' : ?2}}")
    List<PatientVisitCollection> find(ObjectId patientId, List<VisitedFor> visitedFors, Date date, Pageable pageable);

    @Query("{'patientId' : ?0, 'visitedFor': {$in : ?1}, 'updatedTime' : {'$gt' : ?2}}")
    List<PatientVisitCollection> find(ObjectId patientId, List<VisitedFor> visitedFors, Date date, Sort sort);

    @Query("{'recordId' : ?0}")
    PatientVisitCollection findByRecordId(ObjectId recordId);
    
    @Query("{'treatmentId' : ?0}")
    PatientVisitCollection findByTreatmentId(ObjectId treatmentId);

    @Query("{'prescriptionId' : ?0}")
    PatientVisitCollection findByPrescriptionId(ObjectId prescriptionId);

    @Query("{'clinicalNotesId' : ?0}")
    PatientVisitCollection findByClinialNotesId(ObjectId clinicalNotesId);

    @Query(value = "{'doctorId': ?0, 'patientId': ?1, 'hospitalId':?2, 'locationId': ?3, 'visitedFor': {$in : ?4}, 'discarded': ?5}", count = true)
    Integer getVisitCount(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId, List<VisitedFor> visitedFors, boolean discarded);

    @Query(value = "{'patientId':{$in: ?0}, 'doctorId': ?1, 'hospitalId':?2, 'locationId': ?3}", count = true)
    Integer getVisitCount(List<ObjectId> patientIds, ObjectId doctorId, ObjectId hospitalId, ObjectId locationId);

    @Query(value = "{'patientId': ?0, 'visitedFor': {$in : ?1}, 'discarded': ?2}", count = true)
    Integer getVisitCount(ObjectId patientId, List<VisitedFor> visitedFors, boolean discarded);
    
    

}
