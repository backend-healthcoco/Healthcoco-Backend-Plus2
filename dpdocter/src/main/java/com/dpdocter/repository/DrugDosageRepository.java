package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDosageCollection;

public interface DrugDosageRepository extends MongoRepository<DrugDosageCollection, String>, PagingAndSortingRepository<DrugDosageCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugDosageCollection> getGlobalDrugDosage(Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?0}")
    List<DrugDosageCollection> getGlobalDrugDosage(boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': null}")
    List<DrugDosageCollection> getGlobalDrugDosage(Sort sort);

    @Query("{'doctorId': null, 'discarded': ?0}")
    List<DrugDosageCollection> getGlobalDrugDosage(boolean discarded, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
    List<DrugDosageCollection> getGlobalDrugDosage(Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugDosageCollection> getCustomDrugDosage(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugDosageCollection> getCustomGlobalDrugDosage(String doctorId, Date date, boolean discarded, Sort sort);

    Page<DrugDosageCollection> findAll(Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(Date date, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(Date date, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(Date date, boolean discarded, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(Date date, boolean discarded, Sort sort);

    @Query("{'discarded': ?0}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(boolean discarded, Pageable pageable);

    @Query("{'discarded': ?0}")
	List<DrugDosageCollection> getCustomGlobalDrugDosage(boolean discarded, Sort sort);

}
