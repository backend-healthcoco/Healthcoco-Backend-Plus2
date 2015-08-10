package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.NotesCollection;

public interface NotesRepository extends MongoRepository<NotesCollection, String> {

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<NotesCollection> findNotes(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    List<NotesCollection> findNotes(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
	List<NotesCollection> findNotes(Date date, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}, 'isDeleted': ?1}")
	List<NotesCollection> findNotes(Date date, boolean b, Sort sort);
}
