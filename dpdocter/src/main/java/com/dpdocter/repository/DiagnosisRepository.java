package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagnosisCollection;

public interface DiagnosisRepository extends MongoRepository<DiagnosisCollection, String>, PagingAndSortingRepository<DiagnosisCollection, String> {

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null}")
    List<DiagnosisCollection> findGlobalDiagnosis(Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DiagnosisCollection> findGlobalDiagnosis(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DiagnosisCollection> findGlobalDiagnosis(Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DiagnosisCollection> findGlobalDiagnosis(Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': null}")
    List<DiagnosisCollection> findGlobalDiagnosis(Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DiagnosisCollection> findGlobalDiagnosis(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DiagnosisCollection> findGlobalDiagnosis(Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<DiagnosisCollection> findGlobalDiagnosis(Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DiagnosisCollection> findCustomDiagnosis(String doctorId, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<DiagnosisCollection> findCustomGlobalDiagnosis(String doctorId, Boolean discarded, Sort sort);

    Page<DiagnosisCollection> findAll(Pageable pageable);

}
