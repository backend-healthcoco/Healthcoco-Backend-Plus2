package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;

public interface ESComplaintsRepository extends ElasticsearchRepository<ESComplaintsDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"complaint\": \"?0*\",\"doctorId\":null,\"hospitalId\":null,\"locationId\":null}}]}}")
	List<ESComplaintsDocument> findByComplaint(String searchTerm);

	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// public List<ElasticSearchComplaintsDocument> findByQueryAnnotation(String
	// searchTerm);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"updatedTime\": \"?0\",
	// \"discarded\": \"?1\", \"complaint\": \"?2\"}}]}}")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(Date date, boolean discards, String
	// searchTerm, Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)
	// AND complaint:?2*")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(Date date, boolean discards, String
	// searchTerm, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(Date date, Boolean discarded, Pageable
	// pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(Date date, Boolean discarded, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND
	// updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded,
	// Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime:
	// {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded,
	// Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false) AND complaint:?3*) OR ((doctorId: null OR doctorId:
	// \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)
	// AND complaint:?3*)")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded,
	// String searchTerm, Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false) AND complaint:?3*) OR ((doctorId: null OR doctorId:
	// \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)
	// AND complaint:?3*)")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded,
	// String searchTerm, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (
	// (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId:
	// \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO
	// *} AND ( discarded: ?4 OR discarded:false))")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, String locationId, String
	// hospitalId, Date date, Boolean discarded,
	// Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (
	// (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId:
	// \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO
	// *} AND ( discarded: ?4 OR discarded:false))")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, String locationId, String
	// hospitalId, Date date, Boolean discarded,
	// Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND
	// complaint:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId:
	// null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND
	// complaint:?5*)")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, String locationId, String
	// hospitalId, Date date, Boolean discarded,
	// String searchTerm, Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND
	// complaint:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId:
	// null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND
	// complaint:?5*)")
	// public List<ElasticSearchComplaintsDocument>
	// findCustomGlobalComplaints(String doctorId, String locationId, String
	// hospitalId, Date date, Boolean discarded,
	// String searchTerm, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query(" (doctorId: \"\" OR doctorId:null) AND updatedTime: {?0 TO *} AND
	// ( discarded: ?1 OR discarded:false)")
	// public List<ElasticSearchComplaintsDocument> findGlobalComplaints(Date
	// date, Boolean discarded, Pageable pageRequest);
	//
	// @Query(" (doctorId: \"\" OR doctorId:null) AND updatedTime: {?0 TO *} AND
	// ( discarded: ?1 OR discarded:false)")
	// public List<ElasticSearchComplaintsDocument> findGlobalComplaints(Date
	// date, Boolean discarded, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false)")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, Date date, Boolean discarded, Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false)")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, Date date, Boolean discarded, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false) AND complaint:?3*")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, Date date, Boolean discarded, String searchTerm, Pageable
	// pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR
	// discarded:false) AND complaint:?3*")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, String locationId, String hospitalId, Date date, Boolean
	// discarded,
	// Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, String locationId, String hospitalId, Date date, Boolean
	// discarded, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND
	// complaint:?5*")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, String locationId, String hospitalId, Date date, Boolean
	// discarded,
	// String searchTerm, Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND
	// updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND
	// complaint:?5*")
	// public List<ElasticSearchComplaintsDocument> findCustomComplaints(String
	// doctorId, String locationId, String hospitalId, Date date, Boolean
	// discarded,
	// String searchTerm, Sort sort);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query(" (doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *}
	// AND ( discarded: ?1 OR discarded:false) AND complaint:?2*")
	// public List<ElasticSearchComplaintsDocument> findGlobalComplaints(Date
	// date, Boolean discarded, String searchTerm, Pageable pageRequest);
	//
	// @Query("{\"bool\": {\"must\": [{\"match\": {\"complaint\": \"?0\"}}]}}")
	// @Query(" (doctorId: null OR doctorId: \"\" ) AND updatedTime: {?0 TO *}
	// AND ( discarded: ?1 OR discarded:false) AND complaint:?2*")
	// public List<ElasticSearchComplaintsDocument> findGlobalComplaints(Date
	// date, Boolean discarded, String searchTerm, Sort sort);
	//
}
