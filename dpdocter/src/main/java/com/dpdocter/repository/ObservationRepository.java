package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ObservationCollection;

public interface ObservationRepository extends MongoRepository<ObservationCollection, String>, PagingAndSortingRepository<ObservationCollection, String> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
    List<ObservationCollection> findCustomObservations(String doctorId, String locationId, String hospitalId, boolean isDeleted, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<ObservationCollection> findObservations(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    List<ObservationCollection> findObservations(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
	List<ObservationCollection> findObservations(Date date, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}, 'isDeleted': ?1}")
	List<ObservationCollection> findObservations(Date date, boolean b, Sort sort);
}
