package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VaccineBrandCollection;

public interface VaccineBrandRepository extends MongoRepository<VaccineBrandCollection, ObjectId> {

}
