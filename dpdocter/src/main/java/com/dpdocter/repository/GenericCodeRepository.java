package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.GenericCodeCollection;

public interface GenericCodeRepository extends MongoRepository<GenericCodeCollection, String>, PagingAndSortingRepository<GenericCodeCollection, String> {

	@Query("{'code': ?0}")
	GenericCodeCollection findByCode(String code);

}
