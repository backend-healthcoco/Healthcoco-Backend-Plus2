package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CountryCollection;

public interface CountryRepository extends MongoRepository<CountryCollection, String> {

}
