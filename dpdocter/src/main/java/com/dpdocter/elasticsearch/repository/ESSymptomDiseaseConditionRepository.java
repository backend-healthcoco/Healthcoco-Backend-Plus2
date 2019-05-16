package com.dpdocter.elasticsearch.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;

public interface ESSymptomDiseaseConditionRepository extends ElasticsearchRepository<ESSymptomDiseaseConditionDocument, String> {
    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"name\": \"?0*\"}}]}}")
    List<ESSymptomDiseaseConditionDocument> findByQueryAnnotation(String name);

    @Query("{\"bool\": {\"must\": [{\"termsQuery\": {\"id\": \"?0\"}}}}")
	List<ESServicesDocument> findById(Collection<List<String>> serviceIds);

//    @Query("{'id': {'$in': ?0}}")
//    List<SolrSpecialityDocument> findByIds(Collection<String> specialityIds);
//
//    @Query("updatedTime: {?0 TO *}")
//    List<SolrSpecialityDocument> find(Date date, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *}")
//    List<SolrSpecialityDocument> find(Date date, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND speciality:?1*")
//    List<SolrSpecialityDocument> find(Date date, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND speciality: ?1*")
//    List<SolrSpecialityDocument> find(Date date, String searchTerm, Sort sort);
//
//    @Query("{'id': {$in: ?0}}")
//	List<SolrSpecialityDocument> findById(List<String> specialities);
}
