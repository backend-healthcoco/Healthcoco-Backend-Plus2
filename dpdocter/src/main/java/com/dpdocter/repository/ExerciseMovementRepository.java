package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ExerciseMovementCollection;


public interface ExerciseMovementRepository extends MongoRepository<ExerciseMovementCollection, ObjectId>{

}
