package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository extends MongoRepository<DrugDurationUnitCollection, String>,
	PagingAndSortingRepository<DrugDurationUnitCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded,
	    Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2},{'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded,
	    Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': null}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Sort sort);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(boolean discarded, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2},{'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded,
	    Sort sort);

    @Query("{'doctorId': ?0}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Date date, boolean discarded, Sort sort);

    Page<DrugDurationUnitCollection> findAll(Pageable pageable);

}
