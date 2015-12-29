package com.dpdocter.solr.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrSpecialityDocument;

public interface SolrSpecialityRepository extends SolrCrudRepository<SolrSpecialityDocument, String> {
    @Query("speciality : ?0*")
    List<SolrSpecialityDocument> findAll(String speciality);

    @Query("{'id': {'$in': ?0}}")
	List<SolrSpecialityDocument> findByIds(Collection<String> specialityIds);
}
