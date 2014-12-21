package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientInfoCollection;
@Repository
public interface PatientInfoRepository extends MongoRepository<PatientInfoCollection, String>{

}
