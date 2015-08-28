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

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<TemplateCollection> getTemplates(String doctorId, Date date, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    List<TemplateCollection> getTemplates(String doctorId, Date date, boolean isDeleted, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<TemplateCollection> getTemplates(String doctorId, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'isDeleted': ?1}")
    List<TemplateCollection> getTemplates(String doctorId, boolean isDeleted, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, Date date, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, Sort sort,  PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, , 'isDeleted': ?3}")
    List<TemplateCollection> getTemplates(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort,  PageRequest pageRequest);

}
