package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugStrengthUnitCollection;

public interface DrugStrengthUnitRepository extends MongoRepository<DrugStrengthUnitCollection, String> {

}
