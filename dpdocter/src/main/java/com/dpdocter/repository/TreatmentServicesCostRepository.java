package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.TreatmentServicesCostCollection;

public interface TreatmentServicesCostRepository extends MongoRepository<TreatmentServicesCostCollection, ObjectId> {
    
    @Query("{'treatmentServiceId' : ?0}")
    public TreatmentServicesCostCollection findByProductAndServiceId(String productAndServiceId);

    @Query("{'treatmentServiceId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3}")
    public TreatmentServicesCostCollection find(ObjectId id, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

}
