package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CertificateTemplateCollection;

public interface CertificateTemplateRepository extends MongoRepository<CertificateTemplateCollection, ObjectId> {

}
