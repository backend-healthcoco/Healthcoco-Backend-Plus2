package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository extends MongoRepository<DrugDurationUnitCollection, String> {

}
