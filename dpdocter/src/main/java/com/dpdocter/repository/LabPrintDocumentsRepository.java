package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabPrintDocumentsCollection;

public interface LabPrintDocumentsRepository extends MongoRepository<LabPrintDocumentsCollection, ObjectId>, PagingAndSortingRepository<LabPrintDocumentsCollection, ObjectId>{

}
