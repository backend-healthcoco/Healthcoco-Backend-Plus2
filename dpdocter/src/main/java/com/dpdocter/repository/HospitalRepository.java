package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.HospitalCollection;

@Repository
public interface HospitalRepository extends MongoRepository<HospitalCollection, String>{

}
