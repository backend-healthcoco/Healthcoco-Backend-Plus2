package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository extends MongoRepository<DrugDurationUnitCollection, String>,
	PagingAndSortingRepository<DrugDurationUnitCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards,
	    Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(Date date, boolean[] discards, Sort sort);

}
