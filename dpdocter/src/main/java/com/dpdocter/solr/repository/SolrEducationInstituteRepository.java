package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrEducationInstituteDocument;

public interface SolrEducationInstituteRepository extends SolrCrudRepository<SolrEducationInstituteDocument, String> {

	@Query("updatedTime: {?0 TO *}")
	List<SolrEducationInstituteDocument> find(Date date, Pageable pageable);

	@Query("updatedTime: {?0 TO *}")
	List<SolrEducationInstituteDocument> find(Date date, Sort sort);

	@Query("updatedTime: {?0 TO *} AND name: ?1*")
	List<SolrEducationInstituteDocument> find(Date date, String searchTerm, Pageable pageable);

	@Query("updatedTime: {?0 TO *} AND name: ?1*")
	List<SolrEducationInstituteDocument> find(Date date, String searchTerm, Sort sort);

}
