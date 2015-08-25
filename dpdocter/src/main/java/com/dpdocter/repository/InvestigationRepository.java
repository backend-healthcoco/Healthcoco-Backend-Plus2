package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.InvestigationCollection;

public interface InvestigationRepository extends MongoRepository<InvestigationCollection, String>, PagingAndSortingRepository<InvestigationCollection, String> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
    List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId, boolean isDeleted, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<InvestigationCollection> findInvestigations(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    List<InvestigationCollection> findInvestigations(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
    List<InvestigationCollection> findInvestigations(Date date, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}, 'isDeleted': ?1}")
    List<InvestigationCollection> findInvestigations(Date date, boolean b, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'createdTime': {'$gte': ?1}} , {'doctorId': null, 'createdTime': {'$gte': ?1}}]}")
    List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2},{'doctorId': null, 'createdTime': {'$gte': ?1},'isDeleted': ?2}]}")
    List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, Date date, boolean b, Sort sort);
}
