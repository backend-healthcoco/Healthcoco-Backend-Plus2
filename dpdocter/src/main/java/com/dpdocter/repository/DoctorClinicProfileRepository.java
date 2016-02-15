package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorClinicProfileCollection;

public interface DoctorClinicProfileRepository extends MongoRepository<DoctorClinicProfileCollection, String> {
    @Query("{'id': ?0, 'userLocationId': ?1}")
    DoctorClinicProfileCollection findByIdAndLocationId(String id, String userLocationId);

    @Query("{'userLocationId': ?0}")
    DoctorClinicProfileCollection findByLocationId(String userLocationId);
}
