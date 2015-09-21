package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiseasesCollection;

public interface DiseasesRepository extends MongoRepository<DiseasesCollection, String>, PagingAndSortingRepository<DiseasesCollection, String> {

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DiseasesCollection> findGlobalDiseases(Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DiseasesCollection> findGlobalDiseases(Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null}")
    List<DiseasesCollection> findGlobalDiseases(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DiseasesCollection> findGlobalDiseases(Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DiseasesCollection> findCustomDiseases(String doctorId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<DiseasesCollection> findCustomGlobalDiseases(String doctorId, Boolean discarded, Sort sort, PageRequest pageRequest);

}
