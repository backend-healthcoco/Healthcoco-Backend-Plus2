package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ProfessionCollection;

public interface ProfessionRepository extends MongoRepository<ProfessionCollection, ObjectId> {

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<ProfessionCollection> find(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<ProfessionCollection> find(Date date, Sort sort);

}
