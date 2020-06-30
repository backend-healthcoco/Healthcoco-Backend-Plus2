package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PackageDetailObjectCollection;
import com.dpdocter.enums.PackageType;

public interface PackageDetailObjectRepository extends MongoRepository<PackageDetailObjectCollection, ObjectId>{

	PackageDetailObjectCollection findByPackageName(PackageType packageName);

}

