package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDirectionCollection;

public interface DrugDirectionRepository extends MongoRepository<DrugDirectionCollection, String>, PagingAndSortingRepository<DrugDirectionCollection, String> {

	@Query("{'doctorId': null}")
	List<DrugDirectionCollection> getGlobalDrugDirection(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'isDeleted': ?1}")
	List<DrugDirectionCollection> getGlobalDrugDirection(boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<DrugDirectionCollection> getGlobalDrugDirection(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<DrugDirectionCollection> getGlobalDrugDirection(Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugDirectionCollection> getCustomDrugDirection(String doctorId, String hospitalId, String locationId,	Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'isDeleted': ?3}")
	List<DrugDirectionCollection> getCustomDrugDirection(String doctorId, String hospitalId, String locationId,	boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
	List<DrugDirectionCollection> getCustomDrugDirection(String doctorId, String hospitalId, String locationId,	Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
	List<DrugDirectionCollection> getCustomDrugDirection(String doctorId, String hospitalId, String locationId,	Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirection(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirection(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirection(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirection(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

}
