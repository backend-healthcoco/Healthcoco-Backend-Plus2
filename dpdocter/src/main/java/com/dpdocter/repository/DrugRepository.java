package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository extends MongoRepository<DrugCollection, String>, PagingAndSortingRepository<DrugCollection, String> {
    @Query("{'id' : ?0, 'drugCode' : ?1}")
    DrugCollection findByDrugIdAndDrugCode(String id, String drugCode);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<DrugCollection> getDrugs(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugCollection> getDrugs(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': null}")
    List<DrugCollection> getGlobalDrugs(Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugCollection> getGlobalDrugs(boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugCollection> getGlobalDrugs(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugCollection> getGlobalDrugs(Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<DrugCollection> getCustomDrugs(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugCollection> getCustomDrugs(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<DrugCollection> getCustomDrugs(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<DrugCollection> getCustomDrugs(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': null}")
    List<DrugCollection> getGlobalDrugs(Sort sort);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DrugCollection> getGlobalDrugs(boolean discarded, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugCollection> getGlobalDrugs(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugCollection> getGlobalDrugs(Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
    List<DrugCollection> getCustomDrugs(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugCollection> getCustomDrugs(String doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<DrugCollection> getCustomDrugs(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<DrugCollection> getCustomDrugs(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugCollection> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, Sort sort);

    Page<DrugCollection> findAll(Pageable pageRequest);

}
