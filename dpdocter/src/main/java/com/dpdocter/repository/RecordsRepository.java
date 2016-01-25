package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsCollection;

@Repository
public interface RecordsRepository extends MongoRepository<RecordsCollection, String>, PagingAndSortingRepository<RecordsCollection, String> {

    @Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}")
    List<RecordsCollection> findRecords(String patientId, String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'id':?0}")
    RecordsCollection findByRecordId(String recordId);

    @Query(value = "{'doctorId': ?0, 'patientId': ?1, 'hospitalId':?2, 'locationId': ?3, 'discarded': ?4}", count = true)
    Integer getRecordCount(String doctorId, String patientId, String hospitalId, String locationId, boolean discarded);

    @Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}")
    List<RecordsCollection> findRecords(String patientId, String doctorId, String locationId, String hospitalId, Date date, boolean[] discards,
	    Pageable pageRequest);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<RecordsCollection> findRecords(String patientId, Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<RecordsCollection> findRecords(String patientId, Date date, boolean[] discards, Sort sort);

    @Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}, 'inHistory' : {$in: ?6}}")
    List<RecordsCollection> findRecords(String patientId, String doctorId, String locationId, String hospitalId, Date date, boolean[] discards,
	    boolean[] inHistorys, Pageable pageRequest);

    @Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}, 'inHistory' : {$in: ?6}}")
    List<RecordsCollection> findRecords(String patientId, String doctorId, String locationId, String hospitalId, Date date, boolean[] discards,
	    boolean[] inHistorys, Sort sort);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<RecordsCollection> findRecordsByPatientId(String patientId, Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<RecordsCollection> findRecordsByPatientId(String patientId, Date date, boolean[] discards, Sort sort);

    @Query("{'patientId': ?0}")
    List<RecordsCollection> findRecords(String patientId);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}, 'inHistory' : {$in: ?3}}")
    List<RecordsCollection> findRecords(String patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageRequest);

    @Query("{'patientId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}, 'inHistory' : {$in: ?3}}")
    List<RecordsCollection> findRecords(String patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

    @Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
    Integer getRecordsForOtherDoctors(String doctorId, String id, String hospitalId, String locationId);

    @Query(value = "{'patientId': ?0, 'discarded' : ?1}", count = true)
	Integer getRecordCount(String patientId, boolean discarded);

}
