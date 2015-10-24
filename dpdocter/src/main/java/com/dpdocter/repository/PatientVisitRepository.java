package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientVisitCollection;

@Repository
public interface PatientVisitRepository extends MongoRepository<PatientVisitCollection, String>, PagingAndSortingRepository<PatientVisitCollection, String> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PatientVisitCollection> findAll(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}", count = true)
    Integer count(String doctorId, String locationId, String hospitalId);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
	List<PatientVisitCollection> find(String doctorId, String locationId, String hospitalId, String patientId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
	List<PatientVisitCollection> find(String doctorId, String locationId, String hospitalId, String patientId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
	PatientVisitCollection find(String doctorId, String locationId, String hospitalId, String patientId);
}
