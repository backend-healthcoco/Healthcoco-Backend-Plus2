package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.LocationCollection;

@Repository
public interface LocationRepository extends MongoRepository<LocationCollection, String> {

}
