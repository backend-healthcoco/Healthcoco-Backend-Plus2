package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsCollection;

@Repository
public interface RecordsRepository extends MongoRepository<RecordsCollection, String>, PagingAndSortingRepository<RecordsCollection, String> {
    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2,'discarded': ?3}")
    List<RecordsCollection> findRecords(String doctorId, String locationId, String hospitalId, boolean discarded);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'discarded': ?4}")
    List<RecordsCollection> findRecords(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'discarded': ?4}")
    List<RecordsCollection> findRecords(String patientId, String doctorId, String locationId, String hospitalId, boolean discarded, Sort sort);

    @Query("{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'createdTime': {'$gte': ?4}, 'discarded': ?5}")
    List<RecordsCollection> findRecords(String patientId, String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("{'id':?0}")
    RecordsCollection findByRecordId(String recordId);

    @Query(value = "{'doctorId': ?0, 'patientId': ?1, 'hospitalId':?2, 'locationId': ?3, 'discarded': ?4}", count = true)
    Integer getRecordCount(String doctorId, String patientId, String hospitalId, String locationId, boolean discarded);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<RecordsCollection> findAll(String doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'discarded': ?2}")
    List<RecordsCollection> findAll(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<RecordsCollection> findAll(String doctorId, String locationId, String hospitalId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'discarded': ?4}")
    List<RecordsCollection> findAll(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
}
