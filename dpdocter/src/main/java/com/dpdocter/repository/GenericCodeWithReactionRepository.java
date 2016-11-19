package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.GenericCodeCollection;
import com.dpdocter.collections.GenericCodeWithReactionCollection;

public interface GenericCodeWithReactionRepository extends MongoRepository<GenericCodeWithReactionCollection, ObjectId>, PagingAndSortingRepository<GenericCodeWithReactionCollection, ObjectId> {

	@Query("{'codes': {$in : ?0}}")
	GenericCodeWithReactionCollection find(List<String> codes);

}
