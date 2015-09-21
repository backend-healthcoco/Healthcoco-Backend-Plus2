package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDosageCollection;

public interface DrugDosageRepository extends MongoRepository<DrugDosageCollection, String>, PagingAndSortingRepository<DrugDosageCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugDosageCollection> getGlobalDrugDosage(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugDosageCollection> getGlobalDrugDosage(boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, boolean discarded, Sort sort, PageRequest pageRequest);

}
