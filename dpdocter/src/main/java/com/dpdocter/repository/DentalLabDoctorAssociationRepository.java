package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalLabDoctorAssociationCollection;

public interface DentalLabDoctorAssociationRepository extends MongoRepository<DentalLabDoctorAssociationCollection, ObjectId>{

}
