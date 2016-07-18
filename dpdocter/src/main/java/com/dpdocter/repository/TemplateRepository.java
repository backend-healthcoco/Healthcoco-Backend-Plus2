package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.TemplateCollection;

public interface TemplateRepository extends MongoRepository<TemplateCollection, ObjectId>, PagingAndSortingRepository<TemplateCollection, ObjectId> {
    @Query("{'id': ?0, 'doctorId': ?1, 'hospitalId': ?2, 'locationId': ?3}")
    TemplateCollection getTemplate(ObjectId templateId, ObjectId doctorId, ObjectId hospitalId, ObjectId locationId);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<TemplateCollection> getTemplates(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<TemplateCollection> getTemplates(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<TemplateCollection> getTemplates(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<TemplateCollection> getTemplates(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

}
