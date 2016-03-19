package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SpecialityCollection;

public interface SpecialityRepository extends MongoRepository<SpecialityCollection, String> {

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<SpecialityCollection> find(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<SpecialityCollection> find(Date date, Sort sort);

    @Query("{'id': {$in: ?0}}")
	List<SpecialityCollection> findById(List<String> specialities);

}
