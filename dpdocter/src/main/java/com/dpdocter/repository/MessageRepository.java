package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MessageCollection;

public interface MessageRepository extends MongoRepository<MessageCollection, ObjectId> {

	MessageCollection findByMessageId(String messageId);

}
