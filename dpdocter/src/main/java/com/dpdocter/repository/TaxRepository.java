package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TaxCollection;

public interface TaxRepository extends MongoRepository<TaxCollection, ObjectId>{

}
