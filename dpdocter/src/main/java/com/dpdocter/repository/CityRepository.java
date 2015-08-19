package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CityCollection;

public interface CityRepository extends MongoRepository<CityCollection, String> {

}
