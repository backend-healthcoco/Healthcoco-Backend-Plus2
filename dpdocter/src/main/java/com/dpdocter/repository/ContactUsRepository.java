package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ContactUsCollection;

public interface ContactUsRepository extends MongoRepository<ContactUsCollection, ObjectId>, PagingAndSortingRepository<ContactUsCollection, ObjectId> {

}
