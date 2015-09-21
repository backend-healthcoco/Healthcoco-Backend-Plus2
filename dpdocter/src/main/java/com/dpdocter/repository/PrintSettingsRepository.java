package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrintSettingsCollection;

public interface PrintSettingsRepository extends MongoRepository<PrintSettingsCollection, String>, PagingAndSortingRepository<PrintSettingsCollection, String> {

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    PrintSettingsCollection find(String doctorId, String locationId, String hospitalId);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<PrintSettingsCollection> find(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<PrintSettingsCollection> find(String doctorId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<PrintSettingsCollection> find(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<PrintSettingsCollection> find(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<PrintSettingsCollection> find(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<PrintSettingsCollection> find(String doctorId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<PrintSettingsCollection> find(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<PrintSettingsCollection> find(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

}
