package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EducationQualificationCollection;

public interface EducationQualificationRepository
	extends MongoRepository<EducationQualificationCollection, ObjectId>, PagingAndSortingRepository<EducationQualificationCollection, ObjectId> {

}
