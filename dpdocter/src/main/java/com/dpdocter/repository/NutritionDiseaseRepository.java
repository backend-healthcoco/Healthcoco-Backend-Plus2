package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.dpdocter.collections.NutritionDiseaseCollection;

public interface NutritionDiseaseRepository extends MongoRepository<NutritionDiseaseCollection, ObjectId>{

}
