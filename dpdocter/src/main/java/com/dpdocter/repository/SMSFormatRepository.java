package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SMSFormatCollection;

public interface SMSFormatRepository extends MongoRepository<SMSFormatCollection, ObjectId> {

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'type':?3}")
    SMSFormatCollection find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<SMSFormatCollection> find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

}
