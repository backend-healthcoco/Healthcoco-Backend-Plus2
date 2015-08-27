package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDosageCollection;

public interface DrugDosageRepository extends MongoRepository<DrugDosageCollection, String>, PagingAndSortingRepository<DrugDosageCollection, String> {

	@Query("{'doctorId': null}")
	List<DrugDosageCollection> getGlobalDrugDosage(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'isDeleted': ?1}")
	List<DrugDosageCollection> getGlobalDrugDosage(boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<DrugDosageCollection> getGlobalDrugDosage(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<DrugDosageCollection> getGlobalDrugDosage(Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'isDeleted': ?3}")
	List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
	List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
	List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId,	Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId,	boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId,	Date date, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId,	Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

}
