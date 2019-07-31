package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.TemplateCollection;

public interface TemplateRepository extends MongoRepository<TemplateCollection, ObjectId>, PagingAndSortingRepository<TemplateCollection, ObjectId> {
 
}
