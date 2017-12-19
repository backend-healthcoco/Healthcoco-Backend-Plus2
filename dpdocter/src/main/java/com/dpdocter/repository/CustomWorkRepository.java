package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CustomWorkCollection;

public interface CustomWorkRepository extends MongoRepository<CustomWorkCollection, ObjectId>{

}
