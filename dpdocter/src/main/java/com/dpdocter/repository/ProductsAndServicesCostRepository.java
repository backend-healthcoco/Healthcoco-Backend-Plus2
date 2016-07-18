package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ProductsAndServicesCostCollection;

public interface ProductsAndServicesCostRepository extends MongoRepository<ProductsAndServicesCostCollection, ObjectId> {
    
    @Query("{'productAndServiceId' : ?0}")
    public ProductsAndServicesCostCollection findByProductAndServiceId(String productAndServiceId);

    @Query("{'productAndServiceId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3}")
    public ProductsAndServicesCostCollection find(ObjectId productAndServiceId, ObjectId locationId, ObjectId hospitalId, ObjectId doctorId);
}
