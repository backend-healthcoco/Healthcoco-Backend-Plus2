package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SMSFormatCollection;

public interface SMSFormatRepository extends MongoRepository<SMSFormatCollection, String> {

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'type':?3}")
    SMSFormatCollection find(String doctorId, String locationId, String hospitalId, String type);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<SMSFormatCollection> find(String doctorId, String locationId, String hospitalId);

}
