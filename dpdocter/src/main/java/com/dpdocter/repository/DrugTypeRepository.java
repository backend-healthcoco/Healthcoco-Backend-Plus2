package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String>, PagingAndSortingRepository<DrugTypeCollection, String> {

	@Query("{'doctorId': null}")
	List<DrugTypeCollection> getGlobalDrugType(Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'isDeleted': ?1}")
	List<DrugTypeCollection> getGlobalDrugType(boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<DrugTypeCollection> getGlobalDrugType(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<DrugTypeCollection> getGlobalDrugType(Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'isDeleted': ?3}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId,	boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date,	Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date,	boolean isDeleted, Sort sort, PageRequest pageRequest);

}
