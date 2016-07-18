package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.CityCollection;

public interface CityRepository extends MongoRepository<CityCollection, ObjectId>, PagingAndSortingRepository<CityCollection, ObjectId> {

    @Query("{'state': ?0}")
    List<CityCollection> findAll(String state, Sort sort);

}
