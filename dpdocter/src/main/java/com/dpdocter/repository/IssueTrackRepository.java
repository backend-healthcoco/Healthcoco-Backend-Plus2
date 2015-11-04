package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.IssueTrackCollection;

public interface IssueTrackRepository extends MongoRepository<IssueTrackCollection, String>, PagingAndSortingRepository<IssueTrackCollection, String> {

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findAll(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findAll(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
    List<IssueTrackCollection> findAll(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
    List<IssueTrackCollection> findAll(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'status': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': {$in: ?3}}")
    List<IssueTrackCollection> findAll(String doctorId, String status, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'status': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': {$in: ?3}}")
    List<IssueTrackCollection> findAll(String doctorId, String status, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3, 'updatedTime': {'$gte': ?4}, 'discarded': {$in: ?5}}")
    List<IssueTrackCollection> findAll(String doctorId, String locationId, String hospitalId, String status, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3, 'updatedTime': {'$gte': ?4}, 'discarded': {$in: ?5}}")
    List<IssueTrackCollection> findAll(String doctorId, String locationId, String hospitalId, String status, Date date, boolean[] discards, Sort sort);

    @Query("{'status': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findByStatus(String status, Date date, boolean[] discards, Pageable pageable);

    @Query("{'status': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findByStatus(String status, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<IssueTrackCollection> findAll(String doctorId, String locationId, String hospitalId);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<IssueTrackCollection> findAll(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<IssueTrackCollection> findAll(Date date, boolean[] discards, Sort sort);

}
