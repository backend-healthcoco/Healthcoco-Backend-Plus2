package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ObservationCollection;

public interface ObservationRepository extends MongoRepository<ObservationCollection, String>, PagingAndSortingRepository<ObservationCollection, String> {

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null}")
    List<ObservationCollection> findGlobalObservations(Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<ObservationCollection> findGlobalObservations(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<ObservationCollection> findGlobalObservations(Date date, Boolean discarded, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
    List<ObservationCollection> findObservations(Date date, boolean b, Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<ObservationCollection> findGlobalObservations(Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<ObservationCollection> findCustomObservations(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<ObservationCollection> findCustomObservations(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<ObservationCollection> findCustomObservations(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<ObservationCollection> findCustomObservations(String doctorId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Boolean discarded, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': null}")
    List<ObservationCollection> findGlobalObservations(Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<ObservationCollection> findGlobalObservations(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<ObservationCollection> findGlobalObservations(Date date, Boolean discarded, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'isDeleted': ?1}")
    List<ObservationCollection> findObservations(Date date, boolean b, Sort sort);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<ObservationCollection> findGlobalObservations(Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<ObservationCollection> findCustomObservations(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<ObservationCollection> findCustomObservations(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
    List<ObservationCollection> findCustomObservations(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<ObservationCollection> findCustomObservations(String doctorId, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<ObservationCollection> findCustomGlobalObservations(String doctorId, Boolean discarded, Sort sort);

	List<ObservationCollection> find(PageRequest pageRequest);

}
