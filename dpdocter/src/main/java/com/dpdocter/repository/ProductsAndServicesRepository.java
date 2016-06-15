package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ProductsAndServicesCollection;

public interface ProductsAndServicesRepository extends MongoRepository<ProductsAndServicesCollection, String> {
    @Query("{specialityIds : {$in : ?0}}")
    public List<ProductsAndServicesCollection> findAll(List<String> specialityIds);
}
