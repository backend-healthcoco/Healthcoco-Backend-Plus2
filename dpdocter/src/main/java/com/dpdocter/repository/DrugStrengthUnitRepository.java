package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugStrengthUnitCollection;

public interface DrugStrengthUnitRepository extends MongoRepository<DrugStrengthUnitCollection, String> , PagingAndSortingRepository<DrugStrengthUnitCollection, String>{

	@Query("{'doctorId': null}")
	List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'isDeleted': ?1}")
	List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(Date date, boolean isDeleted, Sort sort,	PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'isDeleted': ?3}")
	List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
	List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
	List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

}
