package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsTagsCollection;

@Repository
public interface RecordsTagsRepository extends MongoRepository<RecordsTagsCollection, ObjectId> {
    
	List<RecordsTagsCollection> findByTagsId(ObjectId tagsId, Pageable pageRequest);

    List<RecordsTagsCollection> findByTagsId(ObjectId tagId, Sort sort);

}
