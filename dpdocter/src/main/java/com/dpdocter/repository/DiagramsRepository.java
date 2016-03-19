package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagramsCollection;

public interface DiagramsRepository extends MongoRepository<DiagramsCollection, String>, PagingAndSortingRepository<DiagramsCollection, String> {

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagramsCollection> findGlobalDiagrams(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}")
    List<DiagramsCollection> findCustomDiagrams(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagramsCollection> findGlobalDiagrams(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}}")
    List<DiagramsCollection> findCustomDiagrams(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<DiagramsCollection> findCustomDiagrams(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<DiagramsCollection> findCustomDiagrams(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagramsCollection> findCustomGlobalDiagrams(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagramsCollection> findCustomGlobalDiagrams(Date date, boolean[] discards, Sort sort);

}
