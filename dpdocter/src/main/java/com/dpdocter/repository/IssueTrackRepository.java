package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.IssueTrackCollection;

public interface IssueTrackRepository extends MongoRepository<IssueTrackCollection, ObjectId>, PagingAndSortingRepository<IssueTrackCollection, ObjectId> {

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'status': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, String status, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'status': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, String status, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String status, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String status, Date date, boolean[] discards, Sort sort);

    @Query("{'status': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findByStatus(String status, Date date, boolean[] discards, Pageable pageable);

    @Query("{'status': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<IssueTrackCollection> findByStatus(String status, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<IssueTrackCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<IssueTrackCollection> findAll(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<IssueTrackCollection> findAll(Date date, boolean[] discards, Sort sort);

}
