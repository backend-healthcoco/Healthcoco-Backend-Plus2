package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.HistoryCollection;

public interface HistoryRepository extends MongoRepository<HistoryCollection, ObjectId>, PagingAndSortingRepository<HistoryCollection, ObjectId> {

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
    HistoryCollection findHistory(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId);

    @Query(value = "{'patientId' : ?0, 'doctorId': {$ne : ?1}, 'locationId' : {$ne : ?2}, 'hospitalId' : {$ne : ?3}}", count = true)
    Integer getByPatientIdAndNotEqualToDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

    @Query("{'patientId' : ?0}")
    List<HistoryCollection> findHistory(ObjectId patientId);

    /*@Query(value = "{'patientId': ?0}", fields = "{ 'prescriptions' : 0, 'clinicalNotes' : 0}")
    List<HistoryCollection> findByPatientIdFilterByReports(String patientId);
    
    @Query(value = "{'patientId':?0}", fields = "{ 'prescriptions' : 0, 'reports' : 0}")
    List<HistoryCollection> findByPatientIdFilterByClinicalNotes(String patientId);
    
    @Query(value = "{'patientId':?0}", fields = "{ 'clinicalNotes' : 0, 'reports' : 0}")
    List<HistoryCollection> findByPatientIdFilterByPrescriptions(String patientId);*/

    /*@Query(value = "{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}", fields = "{ 'prescriptions' : 0, 'clinicalNotes' : 0}")
    HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientIdFilterByReports(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String patientId);
    
    @Query(value = "{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}", fields = "{ 'prescriptions' : 0, 'reports' : 0}")
    HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientIdFilterByClinicalNotes(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
        String patientId);
    
    @Query(value = "{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2,'patientId': ?3}", fields = "{ 'clinicalNotes' : 0, 'reports' : 0}")
    HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientIdFilterByPrescriptions(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
        String patientId);*/

    /*@Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3}", count = true)
    Integer getHistoryCount(ObjectId doctorId, String patientId, ObjectId hospitalId, ObjectId locationId);*/

}
