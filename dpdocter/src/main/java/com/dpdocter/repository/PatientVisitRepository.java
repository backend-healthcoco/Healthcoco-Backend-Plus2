package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientVisitCollection;

@Repository
public interface PatientVisitRepository extends MongoRepository<PatientVisitCollection, String>, PagingAndSortingRepository<PatientVisitCollection, String> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PatientVisitCollection> findAll(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PatientVisitCollection> findAll(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
    PatientVisitCollection find(String doctorId, String locationId, String hospitalId, String patientId);

    @Query("{'doctorId': ?0, 'patientId' : ?1, 'updatedTime' : {'$gte' : ?2}}")
    List<PatientVisitCollection> find(String doctorId, String patientId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'patientId' : ?1, 'updatedTime' : {'$gte' : ?2}}")
    List<PatientVisitCollection> find(String doctorId, String patientId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3, 'updatedTime' : {'$gte' : ?4}}")
    List<PatientVisitCollection> find(String doctorId, String locationId, String hospitalId, String patientId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3, 'updatedTime' : {'$gte' : ?4}}")
    List<PatientVisitCollection> find(String doctorId, String locationId, String hospitalId, String patientId, Date date, Sort sort);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}}")
    List<PatientVisitCollection> find(String patientId, Date date, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}}")
    List<PatientVisitCollection> find(String patientId, Date date, Sort sort);

    @Query("{'recordId' : ?0}")
    PatientVisitCollection findByRecordId(String recordId);

    @Query("{'prescriptionId' : ?0}")
    PatientVisitCollection findByPrescriptionId(String id);

    @Query("{'clinicalNotesId' : ?0}")
    PatientVisitCollection findByClinialNotesId(String id);

    @Query(value = "{'doctorId': ?0, 'patientId': ?1, 'hospitalId':?2, 'locationId': ?3, 'discarded': ?4}", count = true)
    Integer getVisitCount(String doctorId, String patientId, String hospitalId, String locationId, boolean discarded);

    @Query(value = "{'patientId':{$in: ?0}, 'doctorId': ?1, 'hospitalId':?2, 'locationId': ?3}", count = true)
    Integer getVisitCount(List<String> patientIds, String doctorId, String hospitalId, String locationId);

}
