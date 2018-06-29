package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ProcedureSheetStructureCollection;

public interface ProcedureSheetStructureRepository extends MongoRepository<ProcedureSheetStructureCollection, ObjectId>{

}
