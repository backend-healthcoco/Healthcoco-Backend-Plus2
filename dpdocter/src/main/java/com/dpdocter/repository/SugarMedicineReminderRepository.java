package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SugarMedicineReminderCollection;

public interface SugarMedicineReminderRepository extends MongoRepository<SugarMedicineReminderCollection, ObjectId>{

}
