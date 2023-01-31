package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MedicineTreatmentSheetCollection;

public interface MedicineSheetRepository extends MongoRepository<MedicineTreatmentSheetCollection, ObjectId> {

}
