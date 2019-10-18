package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AcadamicProfileCollection;

public interface AcadamicProfileRespository extends MongoRepository<AcadamicProfileCollection, ObjectId> {
	
	

}
