package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ContactUsCollection;

public interface ContactUsRepository extends MongoRepository<ContactUsCollection, String>, PagingAndSortingRepository<ContactUsCollection, String> {

}
