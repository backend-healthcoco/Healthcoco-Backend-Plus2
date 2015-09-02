package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugStrengthUnitCollection;

public interface DrugStrengthUnitRepository extends MongoRepository<DrugStrengthUnitCollection, String>,
	PagingAndSortingRepository<DrugStrengthUnitCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugStrengthUnitCollection> getGlobalDrugStrengthUnit(Date date, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugStrengthUnitCollection> getCustomDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2},{'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugStrengthUnitCollection> getCustomGlobalDrugStrengthUnit(String doctorId, String hospitalId, String locationId, Date date, boolean discarded,
	    Sort sort, PageRequest pageRequest);

}
