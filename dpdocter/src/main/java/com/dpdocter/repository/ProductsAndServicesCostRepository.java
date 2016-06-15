package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ProductsAndServicesCostCollection;

public interface ProductsAndServicesCostRepository extends MongoRepository<ProductsAndServicesCostCollection, String> {
    @Override
    @Query("{'productAndServiceId' : ?0}")
    public ProductsAndServicesCostCollection findOne(String productAndServiceId);

    @Query("{'productAndServiceId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2, 'doctorId' : ?3}")
    public ProductsAndServicesCostCollection findOne(String productAndServiceId, String locationId, String hospitalId, String doctorId);
}
