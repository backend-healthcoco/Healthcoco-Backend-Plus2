package com.dpdocter.sms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.beans.SMSTrackDetail;

@Repository
public interface SMSTrackRepository extends MongoRepository<SMSTrackDetail, String> {

}
