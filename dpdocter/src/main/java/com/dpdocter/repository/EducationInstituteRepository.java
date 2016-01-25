package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EducationInstituteCollection;

public interface EducationInstituteRepository extends MongoRepository<EducationInstituteCollection, String>,
	PagingAndSortingRepository<EducationInstituteCollection, String> {

	@Query("{'updatedTime': {'$gt': ?0}}")
	List<EducationInstituteCollection> find(Date date, Pageable pageRequest);

	@Query("{'updatedTime': {'$gt': ?0}}")
	List<EducationInstituteCollection> find(Date date, Sort sort);

}
