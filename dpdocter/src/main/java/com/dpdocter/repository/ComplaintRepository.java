package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ComplaintCollection;

public interface ComplaintRepository extends MongoRepository<ComplaintCollection, String> {
}
