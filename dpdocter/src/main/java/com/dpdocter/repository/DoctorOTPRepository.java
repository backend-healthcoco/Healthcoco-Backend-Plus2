package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorOTPCollection;

public interface DoctorOTPRepository extends MongoRepository<DoctorOTPCollection, String>, PagingAndSortingRepository<DoctorOTPCollection, String> {

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3}")
    List<DoctorOTPCollection> find(String doctorId, String locationId, String hospitalId, String patientId, Sort sort);

}
