package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.TemplateCollection;

public interface TemplateRepository extends MongoRepository<TemplateCollection, String>, PagingAndSortingRepository<TemplateCollection, String> {
    @Query("{'id': ?0, 'doctorId': ?1, 'hospitalId': ?2, 'locationId': ?3}")
    TemplateCollection getTemplate(String templateId, String doctorId, String hospitalId, String locationId);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<TemplateCollection> getTemplates(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<TemplateCollection> getTemplates(String doctorId, Date date, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<TemplateCollection> getTemplates(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<TemplateCollection> getTemplates(String doctorId, boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort,
	    PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, , 'discarded': ?3}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort, PageRequest pageRequest);

}
