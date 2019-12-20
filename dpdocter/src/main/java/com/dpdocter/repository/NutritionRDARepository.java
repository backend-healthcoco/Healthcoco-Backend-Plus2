package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.NutritionRDACollection;

@Repository
public interface NutritionRDARepository extends MongoRepository<NutritionRDACollection, ObjectId> {

	NutritionRDACollection findByCountryIdAndGenderAndType(String countryId, String group, String category);
}
