package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.TreatmentServicesCollection;

public interface TreatmentServicesRepository extends MongoRepository<TreatmentServicesCollection, ObjectId> {
    @Query("{'specialityIds' : {$in : ?0}}")
    public List<TreatmentServicesCollection> findAll(List<ObjectId> specialityIds);
}
