package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.OTPCollection;

public interface OTPRepository extends MongoRepository<OTPCollection, String>, PagingAndSortingRepository<OTPCollection, String> {

	
}
