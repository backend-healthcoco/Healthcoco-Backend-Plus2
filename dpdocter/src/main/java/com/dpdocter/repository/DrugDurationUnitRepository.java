package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository extends MongoRepository<DrugDurationUnitCollection, String>, PagingAndSortingRepository<DrugDurationUnitCollection, String> {
   
	@Query("{'doctorId': null}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'isDeleted': ?1}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean isDeleted, Sort sort,	PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'isDeleted': ?3}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

}
