package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository extends MongoRepository<DrugDurationUnitCollection, String>,
	PagingAndSortingRepository<DrugDurationUnitCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2},{'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded,
	    Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, boolean discarded, Sort sort,
			PageRequest pageRequest);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, Sort sort,
			PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, boolean discarded, Sort sort,
			PageRequest pageRequest);

}
