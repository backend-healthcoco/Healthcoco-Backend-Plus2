package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ExerciseCollection;

public interface ExerciseRepository extends MongoRepository<ExerciseCollection, ObjectId>{

}
