package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.NotesCollection;

public interface NotesRepository extends MongoRepository<NotesCollection, ObjectId>, PagingAndSortingRepository<NotesCollection, ObjectId> {

	List<NotesCollection> findByIdIn(List<String> specialNotes);
}
