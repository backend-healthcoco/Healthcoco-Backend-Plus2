package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabTestCollection;

public interface LabTestRepository extends MongoRepository<LabTestCollection, String>, PagingAndSortingRepository<LabTestCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
	List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
	List<LabTestCollection> getGlobalLabTests(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
	List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Pageable pageable);

	@Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
	List<LabTestCollection> getCustomGlobalLabTests(Date date, boolean[] discards, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Date date, boolean[] discards, Sort sort);
	
	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

}
