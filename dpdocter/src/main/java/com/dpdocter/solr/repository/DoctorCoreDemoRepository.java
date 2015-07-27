package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.DoctorCoreDemoDocument;

public interface DoctorCoreDemoRepository extends SolrCrudRepository<DoctorCoreDemoDocument, String> {

    @Query("name:*?0* OR title:*?0* OR doctorSpecification:*?0*")
    public List<DoctorCoreDemoDocument> findByQueryAnnotation(String searchTerm);
}
