package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.TemplateCollection;

public interface TemplateRepository extends MongoRepository<TemplateCollection, String> {
	@Query("{'id' : ?0, 'doctorId' : ?1, 'hospitalId' : ?2, 'locationId' : ?3}")
	TemplateCollection getTemplate(String templateId, String doctorId, String hospitalId, String locationId);
}
