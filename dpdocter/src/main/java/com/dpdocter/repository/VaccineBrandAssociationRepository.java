package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VaccineBrandAssociationCollection;

public interface VaccineBrandAssociationRepository
		extends MongoRepository<VaccineBrandAssociationCollection, ObjectId> {

}
