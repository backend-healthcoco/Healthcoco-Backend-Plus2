package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.TagsCollection;

@Repository
public interface TagsRepository extends MongoRepository<TagsCollection, ObjectId> {

}
