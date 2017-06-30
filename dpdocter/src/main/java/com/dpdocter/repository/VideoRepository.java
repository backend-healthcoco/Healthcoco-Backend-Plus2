package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VideoCollection;

public interface VideoRepository extends MongoRepository<VideoCollection, ObjectId>{

}
