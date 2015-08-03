package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DiagramsCollection;

public interface DiagramsRepository extends MongoRepository<DiagramsCollection, String> {

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<DiagramsCollection> findDiagrams(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    List<DiagramsCollection> findDiagrams(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
    List<DiagramsCollection> findGlobalDiagrams(Date date, Sort sort);
}
