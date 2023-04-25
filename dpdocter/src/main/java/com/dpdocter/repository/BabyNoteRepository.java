package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BabyNoteCollection;

public interface BabyNoteRepository extends MongoRepository<BabyNoteCollection, ObjectId> {

}
