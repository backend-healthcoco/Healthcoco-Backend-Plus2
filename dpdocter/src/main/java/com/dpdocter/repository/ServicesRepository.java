package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ServicesCollection;

@Repository
public interface ServicesRepository extends MongoRepository<ServicesCollection, ObjectId> {

	List<ServicesCollection> findByServiceIn(List<String> services);

	List<ServicesCollection> findBySpecialityIdsIn(List<ObjectId> specialityIds);

  
}
