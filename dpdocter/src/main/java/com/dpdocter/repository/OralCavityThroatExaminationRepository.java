package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OralCavityAndThroatExaminationCollection;

public interface OralCavityThroatExaminationRepository  extends MongoRepository<OralCavityAndThroatExaminationCollection, ObjectId>{

}
