package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.MedicalCouncilCollection;

public interface MedicalCouncilRepository extends MongoRepository<MedicalCouncilCollection, String> {

	@Query("{'updatedTime': {'$gt': ?0}}")
	List<MedicalCouncilCollection> find(Date date, Pageable pageRequest);
	
	@Query("{'updatedTime': {'$gt': ?0}}")
	List<MedicalCouncilCollection> find(Date date, Sort sort);

}
