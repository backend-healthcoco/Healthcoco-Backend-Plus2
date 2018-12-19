package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ClinicalNotesSequenceCollection;

public interface ClinicalNotesSequenceRepository extends MongoRepository<ClinicalNotesSequenceCollection, ObjectId>{

}
