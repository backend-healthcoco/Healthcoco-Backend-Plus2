package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String>, PagingAndSortingRepository<DrugTypeCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugTypeCollection> getGlobalDrugType(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugTypeCollection> getGlobalDrugType(boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
	List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, boolean discarded, Sort sort, PageRequest pageRequest);

}
