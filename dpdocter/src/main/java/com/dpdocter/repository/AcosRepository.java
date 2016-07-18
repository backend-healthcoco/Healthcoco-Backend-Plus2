package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AcosCollection;

public interface AcosRepository extends MongoRepository<AcosCollection, ObjectId> {

	@Query("{'id': {$in: ?0}}")
	List<AcosCollection> findAll(List<ObjectId> acosIds);

}
