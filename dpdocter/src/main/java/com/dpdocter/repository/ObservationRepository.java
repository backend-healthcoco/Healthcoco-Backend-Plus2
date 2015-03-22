package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ObservationCollection;

public interface ObservationRepository extends MongoRepository<ObservationCollection, String>{

}
