package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.CityCollection;

public interface CityRepository extends MongoRepository<CityCollection, ObjectId>, PagingAndSortingRepository<CityCollection, ObjectId> {

    List<CityCollection> findByState(String state, Sort sort);

	CityCollection findByCity(String city);

}
