package com.dpdocter.solr.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrSpecialityDocument;

public interface SolrSpecialityRepository extends SolrCrudRepository<SolrSpecialityDocument, String> {
    @Query("speciality : ?0*")
    List<SolrSpecialityDocument> findAll(String speciality);

    @Query("{'id': {'$in': ?0}}")
	List<SolrSpecialityDocument> findByIds(Collection<String> specialityIds);

    @Query("updatedTime: {?0 TO *}")
	List<SolrSpecialityDocument> find(Date date, Pageable pageable);

    @Query("updatedTime: {?0 TO *}")
	List<SolrSpecialityDocument> find(Date date, Sort sort);

    @Query("updatedTime: {?0 TO *} AND speciality: ?1*")
	List<SolrSpecialityDocument> find(Date date, String searchTerm, Pageable pageable);

    @Query("updatedTime: {?0 TO *} AND speciality: ?1*")
	List<SolrSpecialityDocument> find(Date date, String searchTerm, Sort sort);
}
