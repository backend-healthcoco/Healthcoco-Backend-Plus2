package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ProcedureSheetCollection;

public interface ProcedureSheetRepository extends MongoRepository<ProcedureSheetCollection, ObjectId>{

}
