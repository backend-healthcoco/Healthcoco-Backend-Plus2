package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RecipeTemplateCollection;

public interface RecipeTemplateRepository extends MongoRepository<RecipeTemplateCollection, ObjectId> {

	RecipeTemplateCollection findByNameAndDoctorIdAndLocationIdAndHospitalId(String name, ObjectId doctorId,
			ObjectId locatioNid, ObjectId hospitalId);

}
