package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LanguageCollection;

public interface LanguageRepository extends MongoRepository<LanguageCollection,ObjectId>{

	LanguageCollection findByName(String name);

}
