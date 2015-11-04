package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ReferencesCollection;

@Repository
public interface ReferenceRepository extends MongoRepository<ReferencesCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findAll(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findAll(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<ReferencesCollection> findCustom(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<ReferencesCollection> findCustom(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
    List<ReferencesCollection> findCustom(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
    List<ReferencesCollection> findCustom(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findCustomGlobal(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<ReferencesCollection> findCustomGlobal(Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}} , {'doctorId': null, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}]}")
    List<ReferencesCollection> findCustomGlobal(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}} , {'doctorId': null, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}]}")
    List<ReferencesCollection> findCustomGlobal(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
    List<ReferencesCollection> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
    List<ReferencesCollection> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

}
