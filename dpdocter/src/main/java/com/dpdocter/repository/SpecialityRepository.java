package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SpecialityCollection;

public interface SpecialityRepository extends MongoRepository<SpecialityCollection, String> {

}
