package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrintSettingsCollection;

public interface PrintSettingsRepository extends MongoRepository<PrintSettingsCollection, String>, PagingAndSortingRepository<PrintSettingsCollection, String> {

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<PrintSettingsCollection> getSettings(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': {$in: ?2}}")
    List<PrintSettingsCollection> getSettings(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
    List<PrintSettingsCollection> getSettings(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': {$in: ?4}}")
    List<PrintSettingsCollection> getSettings(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<PrintSettingsCollection> findAll(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': {$in: ?1}}")
    List<PrintSettingsCollection> findAll(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'componentType': ?3}")
    PrintSettingsCollection getSettings(String doctorId, String locationId, String hospitalId, String type);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    PrintSettingsCollection getSettings(String doctorId, String locationId, String hospitalId);

}
