package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PCUserCollection;

/**
 * @author veeraj
 */
@Repository
public interface PCUserRepository extends MongoRepository<PCUserCollection, ObjectId> {

	public PCUserCollection findByMrCode(String mrCode);
}
