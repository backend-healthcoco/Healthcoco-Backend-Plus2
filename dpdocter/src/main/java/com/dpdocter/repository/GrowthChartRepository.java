package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.GrowthChartCollection;

public interface GrowthChartRepository extends MongoRepository<GrowthChartCollection, ObjectId>{

}
