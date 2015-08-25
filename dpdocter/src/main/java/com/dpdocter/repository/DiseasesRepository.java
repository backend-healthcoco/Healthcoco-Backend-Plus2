package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DiseasesCollection;

public interface DiseasesRepository extends MongoRepository<DiseasesCollection, String> {

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2}")
    List<DiseasesCollection> findDiseases(String doctorId, String locationId, String hospitalId);

    @Query("{'$or': [{'doctorId': ?0, 'createdTime': {'$gte': ?1}} , {'doctorId': null, 'createdTime': {'$gte': ?1}}]}")
    List<DiseasesCollection> findGlobalCustomDiseases(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2},{'doctorId': null, 'createdTime': {'$gte': ?1},'isDeleted': ?2}]}")
    List<DiseasesCollection> findGlobalCustomDiseases(String doctorId, Date date, boolean isDeleted, Sort sort);

}
