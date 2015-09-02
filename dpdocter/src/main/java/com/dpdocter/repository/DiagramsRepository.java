package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagramsCollection;

public interface DiagramsRepository extends MongoRepository<DiagramsCollection, String>, PagingAndSortingRepository<DiagramsCollection, String> {

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
	List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, Date date,	Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
	List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
	List<DiagramsCollection> findGlobalDiagrams(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
	List<DiagramsCollection> findGlobalDiagrams(Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null}")
	List<DiagramsCollection> findGlobalDiagrams(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'discarded': ?1}")
	List<DiagramsCollection> findGlobalDiagrams(Boolean discarded, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
	List<DiagramsCollection> findCustomDiagrams(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
	List<DiagramsCollection> findCustomDiagrams(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	List<DiagramsCollection> findCustomDiagrams(String doctorId, String locationId, String hospitalId, Sort sort,
			PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
	List<DiagramsCollection> findCustomDiagrams(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, Sort sort,	PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
	List<DiagramsCollection> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

}
