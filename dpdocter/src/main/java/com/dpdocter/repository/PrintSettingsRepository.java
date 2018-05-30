package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrintSettingsCollection;

public interface PrintSettingsRepository extends MongoRepository<PrintSettingsCollection, ObjectId>, PagingAndSortingRepository<PrintSettingsCollection, ObjectId> {

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<PrintSettingsCollection> getSettings(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<PrintSettingsCollection> getSettings(ObjectId doctorId, Date date, boolean[] discards, Sort sort);
    
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<PrintSettingsCollection> getSettings(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<PrintSettingsCollection> getSettings(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);
    

    @Query("{ 'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<PrintSettingsCollection> getSettings( ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}}")
    List<PrintSettingsCollection> getSettings( ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<PrintSettingsCollection> findAll(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<PrintSettingsCollection> findAll(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'componentType': ?3}")
    PrintSettingsCollection getSettings(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    PrintSettingsCollection getSettings(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
    
    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    PrintSettingsCollection getSettings( ObjectId locationId, ObjectId hospitalId);

    @Query("{'locationId': ?0}")
    List<PrintSettingsCollection> findByLocationId(ObjectId locationId);

}
