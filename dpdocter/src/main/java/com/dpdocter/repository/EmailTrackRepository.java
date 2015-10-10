package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EmailTrackCollection;

public interface EmailTrackRepository extends MongoRepository<EmailTrackCollection, String>, PagingAndSortingRepository<EmailTrackCollection, String> {

    @Override
    Page<EmailTrackCollection> findAll(Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    List<EmailTrackCollection> findAll(String locationId, String hospitalId, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    List<EmailTrackCollection> findAll(String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0}")
    List<EmailTrackCollection> findAll(String doctorId, Pageable pageRequest);

    @Query("{'doctorId': ?0}")
    List<EmailTrackCollection> findAll(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<EmailTrackCollection> findAll(String doctorId, String locationId, String hospitalId, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<EmailTrackCollection> findAll(String doctorId, String locationId, String hospitalId, Sort sort);

}
