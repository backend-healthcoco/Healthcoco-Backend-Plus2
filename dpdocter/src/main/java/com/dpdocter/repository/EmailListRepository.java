package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.EmailListCollection;
@Repository
public interface EmailListRepository extends MongoRepository<EmailListCollection, ObjectId> {

    EmailListCollection findByLocationId(ObjectId locationId);

}
