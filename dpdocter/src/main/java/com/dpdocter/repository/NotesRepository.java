package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.NotesCollection;

public interface NotesRepository extends MongoRepository<NotesCollection, String>, PagingAndSortingRepository<NotesCollection, String> {

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<NotesCollection> findGlobalNotes(Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<NotesCollection> findGlobalNotes(Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null}")
    List<NotesCollection> findGlobalNotes(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'discarded': ?1}")
    List<NotesCollection> findGlobalNotes(Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<NotesCollection> findCustomNotes(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<NotesCollection> findCustomNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<NotesCollection> findCustomNotes(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<NotesCollection> findCustomNotes(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<NotesCollection> findCustomNotes(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<NotesCollection> findCustomNotes(String doctorId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<NotesCollection> findCustomNotes(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<NotesCollection> findCustomNotes(String doctorId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<NotesCollection> findCustomGlobalNotes(String doctorId, Boolean discarded, Sort sort, PageRequest pageRequest);
}
