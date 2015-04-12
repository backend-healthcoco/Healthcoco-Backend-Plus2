package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorCollection;

@Repository
public interface DoctorRepository extends MongoRepository<DoctorCollection, String> {
}
