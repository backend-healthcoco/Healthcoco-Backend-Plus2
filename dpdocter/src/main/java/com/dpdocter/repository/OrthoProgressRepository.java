package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.OrthoProgressCollection;

public interface OrthoProgressRepository extends MongoRepository<OrthoProgressCollection, ObjectId>,
		PagingAndSortingRepository<OrthoProgressCollection, ObjectId> {

}
