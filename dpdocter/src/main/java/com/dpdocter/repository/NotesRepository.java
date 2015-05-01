package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NotesCollection;

public interface NotesRepository extends MongoRepository<NotesCollection, String> {

}
