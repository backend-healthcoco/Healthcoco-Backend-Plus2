package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ComplaintCollection;

public interface ComplaintRepository extends MongoRepository<ComplaintCollection, String>, PagingAndSortingRepository<ComplaintCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ComplaintCollection> findGlobalComplaints(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ComplaintCollection> findGlobalComplaints(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ComplaintCollection> findCustomGlobalComplaints(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ComplaintCollection> findCustomGlobalComplaints(Date date, boolean[] discards, Sort sort);

}
