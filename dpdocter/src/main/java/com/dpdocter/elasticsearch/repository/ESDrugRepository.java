package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDrugDocument;

public interface ESDrugRepository extends ElasticsearchRepository<ESDrugDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match\": {\"drugTypeId\": \"?0\"}}]}}")
	List<ESDrugDocument> findBydrugTypeId(String drugTypeId);
//    @Query("drugName:?0*")
//    public List<ESDrugDocument> find(String searchTerm);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<ESDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<ESDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND (discarded: ?1 OR discarded:false) AND drugName:?2")
//    public List<ESDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND discarded:false AND (drugName:?2* OR explanation:?2* OR drugCode:?2*)")
//    public List<ESDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, String searchTerm, Sort sort);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:?3* OR explanation:?3* OR drugCode:?3*)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:?3* OR explanation:?3* OR drugCode:?3*))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:?3* OR explanation:?3* OR drugCode:?3*)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:?3* OR explanation:?3* OR drugCode:?3*))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:?5* OR explanation:?5* OR drugCode:?5*)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:?5* OR explanation:?5* OR drugCode:?5*))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2*  AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:?5* OR explanation:?5* OR drugCode:?5*)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:?5* OR explanation:?5* OR drugCode:?5*))")
//    public List<ESDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
//	    Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<ESDrugDocument> getGlobalDrugs(Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded: false )")
//    public List<ESDrugDocument> getGlobalDrugs(Date date, boolean discarded, Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND (drugName:?2* OR explanation:?2* OR drugCode:?2*)")
//    public List<ESDrugDocument> getGlobalDrugs(Date date, boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND (drugName:?2* OR explanation:?2* OR drugCode:?2*)")
//    public List<ESDrugDocument> getGlobalDrugs(Date date, boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:?3* OR explanation:?3* OR drugCode:?3*)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:?3* OR explanation:?3* OR drugCode:?3*)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:?5* OR explanation:?5* OR drugCode:?5*)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
//	    Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND hospitalId:*?1* AND locationId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:?5* OR explanation:?5* OR drugCode:?5*)")
//    public List<ESDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
//	    Sort sort);
}
