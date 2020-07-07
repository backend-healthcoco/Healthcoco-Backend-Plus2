package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PackageDetailObjectCollection;
import com.dpdocter.enums.PackageType;

@Repository
public interface PackageDetailObjectRepository extends MongoRepository<PackageDetailObjectCollection, ObjectId>{

	PackageDetailObjectCollection findByPackageName(PackageType packageName);

}

