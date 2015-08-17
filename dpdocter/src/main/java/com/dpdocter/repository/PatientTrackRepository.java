package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientTrackCollection;

@Repository
public interface PatientTrackRepository extends MongoRepository<PatientTrackCollection, String> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PatientTrackCollection> findAll(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}", count = true)
    Integer count(String doctorId, String locationId, String hospitalId);
}
