package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String>, PagingAndSortingRepository<DrugTypeCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?2}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': {$in: ?4}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': {$in: ?2}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getCustomGlobalDrugType(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getCustomGlobalDrugType(Date date, boolean[] discards, Sort sort);

    @Query("{'type': ?0, 'doctorId': null, 'locationId': null, 'hospitalId': null}")
    DrugTypeCollection findByType(String string);

}
