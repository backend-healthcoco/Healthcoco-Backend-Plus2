package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabTestCollection;

public interface LabTestRepository extends MongoRepository<LabTestCollection, String>, PagingAndSortingRepository<LabTestCollection, String> {

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Sort sort);
 
    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': {$in: ?3}}")
    List<LabTestCollection> getCustomLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': {$in: ?3}}")
    List<LabTestCollection> getCustomLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?2},'discarded': {$in: ?3}}]}")
    List<LabTestCollection> getCustomGlobalLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?2},'discarded': {$in: ?3}}]}")
    List<LabTestCollection> getCustomGlobalLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

}
