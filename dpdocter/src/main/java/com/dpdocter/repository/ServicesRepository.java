package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ServicesCollection;

public interface ServicesRepository extends MongoRepository<ServicesCollection, ObjectId> {

	@Query("{'service' : {$in : ?0}}")
	List<ServicesCollection> findbyService(List<String> services);

  
}
