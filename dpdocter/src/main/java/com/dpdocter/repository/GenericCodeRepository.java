package com.dpdocter.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.GenericCodeCollection;

public interface GenericCodeRepository extends MongoRepository<GenericCodeCollection, ObjectId>, PagingAndSortingRepository<GenericCodeCollection, ObjectId> {

	@Query("{'code': ?0}")
	GenericCodeCollection findByCode(String code);
	
	@Query("{'code': {$in : ?0}}")
	List<GenericCodeCollection> findByCodes(Collection<String> genericCodes);

}
