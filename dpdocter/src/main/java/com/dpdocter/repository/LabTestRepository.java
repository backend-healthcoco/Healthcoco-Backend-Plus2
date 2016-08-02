package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabTestCollection;

public interface LabTestRepository extends MongoRepository<LabTestCollection, ObjectId>, PagingAndSortingRepository<LabTestCollection, ObjectId> {

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Sort sort);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<LabTestCollection> getCustomLabTests(ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<LabTestCollection> getCustomLabTests(ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<LabTestCollection> getCustomGlobalLabTests(ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<LabTestCollection> getCustomGlobalLabTests(ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'locationId': ?0}")
    List<LabTestCollection> findByLocationId(ObjectId hospitalId, ObjectId locationId, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}, 'testId': {$in: ?4}}")
	List<LabTestCollection> getCustomLabTests(ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Collection<ObjectId> testIds, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}, 'testId': {$in: ?4}}")
	List<LabTestCollection> getCustomLabTests(ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Collection<ObjectId> testIds, Sort sort);

    @Query(value = "{'hospitalId' : ?0, 'locationId' : ?1}", count = true)
    Integer getLabTestCount(ObjectId hospitalId, ObjectId locationId);

}
