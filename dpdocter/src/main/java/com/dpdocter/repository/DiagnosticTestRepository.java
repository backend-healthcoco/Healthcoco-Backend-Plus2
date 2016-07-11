package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DiagnosticTestCollection;

public interface DiagnosticTestRepository extends MongoRepository<DiagnosticTestCollection, String> {

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagnosticTestCollection> getCustomGlobal(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagnosticTestCollection> getCustomGlobal(Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<DiagnosticTestCollection> getCustomGlobal(String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<DiagnosticTestCollection> getCustomGlobal(String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<DiagnosticTestCollection> getCustom(String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'hospitalId': ?0, 'locationId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<DiagnosticTestCollection> getCustom(String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagnosticTestCollection> getGlobal(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DiagnosticTestCollection> getGlobal(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagnosticTestCollection> getCustomGlobalForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagnosticTestCollection> getCustomGlobalForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'testName' : {$regex : '^?2', $options : 'i'}}")
	List<DiagnosticTestCollection> getCustomGlobalForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'testName' : {$regex : '^?2', $options : 'i'}}")
	List<DiagnosticTestCollection> getCustomGlobalForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagnosticTestCollection> getCustomForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagnosticTestCollection> getCustomForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'testName' : {$regex : '^?2', $options : 'i'}}")
	List<DiagnosticTestCollection> getCustomForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'testName' : {$regex : '^?2', $options : 'i'}}")
	List<DiagnosticTestCollection> getCustomForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagnosticTestCollection> getGlobalForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagnosticTestCollection> getGlobalForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'testName' : {$regex : '^?2', $options : 'i'}}")
	List<DiagnosticTestCollection> getGlobalForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'testName' : {$regex : '^?2', $options : 'i'}}")
	List<DiagnosticTestCollection> getGlobalForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

}
