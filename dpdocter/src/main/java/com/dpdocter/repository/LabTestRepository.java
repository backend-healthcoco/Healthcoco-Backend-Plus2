package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabTestCollection;

public interface LabTestRepository extends MongoRepository<LabTestCollection, String>, PagingAndSortingRepository<LabTestCollection, String> {

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Sort sort);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<LabTestCollection> getCustomLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<LabTestCollection> getCustomLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<LabTestCollection> getCustomGlobalLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<LabTestCollection> getCustomGlobalLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'testName': ?0}")
    List<LabTestCollection> findByTestName(String testName);

    @Query("{'locationId': ?0}")
    List<LabTestCollection> findByLocationId(String id);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}, 'testId': {$in: ?4}}")
	List<LabTestCollection> getCustomLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Collection<String> testIds, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}, 'testId': {$in: ?4}}")
	List<LabTestCollection> getCustomLabTests(String hospitalId, String locationId, Date date, boolean[] discards, Collection<String> testIds, Sort sort);

}
