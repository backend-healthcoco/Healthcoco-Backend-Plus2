package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ClinicContactUsCollection;

public interface ClinicContactUsRepository extends MongoRepository<ClinicContactUsCollection, ObjectId> {

	public ClinicContactUsCollection findByLocationName(String locationName);

}
