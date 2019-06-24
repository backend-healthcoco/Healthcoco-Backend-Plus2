package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SugarSettingCollection;

public interface SugarSettingRepository extends MongoRepository<SugarSettingCollection, ObjectId>{

}
