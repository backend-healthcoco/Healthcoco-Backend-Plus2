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

import com.dpdocter.collections.RecordsCollection;

@Repository
public interface RecordsRepository extends MongoRepository<RecordsCollection, ObjectId>, PagingAndSortingRepository<RecordsCollection, ObjectId> {

    @Query("{'$or': [{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'patientId': ?0, 'prescribedByDoctorId': ?1, 'prescribedByLocationId': ?2, 'prescribedByHospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}]}")
    List<RecordsCollection> findRecords(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query(value = "{'$or': [{'doctorId': ?0, 'patientId': ?1, 'hospitalId':?2, 'locationId': ?3, 'discarded': ?4},{'prescribedByDoctorId': ?0, 'patientId': ?1, 'prescribedByHospitalId': ?2, 'prescribedByLocationId': ?3, 'discarded': ?4}]}", count = true)
    Integer getRecordCount(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId, boolean discarded);

    @Query("{'$or': [{'patientId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'patientId': ?0, 'prescribedByDoctorId': ?1, 'prescribedByLocationId': ?2, 'prescribedByHospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}]}")
    List<RecordsCollection> findRecords(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageRequest);

    @Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
    Integer getRecordsForOtherDoctors(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

    @Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
	Integer getRecordsForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

}
