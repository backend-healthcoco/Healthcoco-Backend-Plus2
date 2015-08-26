package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.InvestigationCollection;

public interface InvestigationRepository extends MongoRepository<InvestigationCollection, String>, PagingAndSortingRepository<InvestigationCollection, String> {
    
	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId,	Date date, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId,	Date date, Boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<InvestigationCollection> findGlobalInvestigations(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<InvestigationCollection> findGlobalInvestigations(Date date, Boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null}")
	List<InvestigationCollection> findGlobalInvestigations(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'isDeleted': ?1}")
	List<InvestigationCollection> findGlobalInvestigations(Boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}}")
	List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId,Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4}")
	List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId,Date date, Boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId,Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
	List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId,Boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId,	Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Boolean isDeleted, Sort sort, PageRequest pageRequest);
}
