package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDosageCollection;

public interface DrugDosageRepository extends MongoRepository<DrugDosageCollection, String>, PagingAndSortingRepository<DrugDosageCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(Date date, boolean[] discards, Sort sort);

}
