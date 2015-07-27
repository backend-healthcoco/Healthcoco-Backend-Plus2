package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorCollection;

@Repository
public interface DoctorRepository extends MongoRepository<DoctorCollection, String> {

    @Query("{'userId': ?0}")
    DoctorCollection findByUserId(String doctorId);
}
