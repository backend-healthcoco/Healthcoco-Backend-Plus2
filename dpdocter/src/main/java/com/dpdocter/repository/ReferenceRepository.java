package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ReferencesCollection;

@Repository
public interface ReferenceRepository extends MongoRepository<ReferencesCollection, ObjectId> {

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findAll(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findAll(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ReferencesCollection> findCustom(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ReferencesCollection> findCustom(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<ReferencesCollection> findCustom(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<ReferencesCollection> findCustom(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findCustomGlobal(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findCustomGlobal(Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}} , {'doctorId': null, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}]}")
    List<ReferencesCollection> findCustomGlobal(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}} , {'doctorId': null, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}]}")
    List<ReferencesCollection> findCustomGlobal(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<ReferencesCollection> findCustomGlobal(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<ReferencesCollection> findCustomGlobal(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'reference': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3} , {'reference': ?0, 'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	ReferencesCollection find(String reference, String doctorId, String locationId, String hospitalId);

}
