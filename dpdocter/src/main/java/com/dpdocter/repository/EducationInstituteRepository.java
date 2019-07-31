package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EducationInstituteCollection;

public interface EducationInstituteRepository extends MongoRepository<EducationInstituteCollection, ObjectId>, PagingAndSortingRepository<EducationInstituteCollection, ObjectId> {

}
