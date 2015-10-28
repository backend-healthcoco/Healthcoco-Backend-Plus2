package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EducationQualificationCollection;

public interface EducationQualificationRepository extends MongoRepository<EducationQualificationCollection, String>, PagingAndSortingRepository<EducationQualificationCollection, String> {

}
