package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsTagsCollection;

@Repository
public interface RecordsTagsRepository extends MongoRepository<RecordsTagsCollection, String> {
	List<RecordsTagsCollection> findByTagsId(String tagsId);
}
