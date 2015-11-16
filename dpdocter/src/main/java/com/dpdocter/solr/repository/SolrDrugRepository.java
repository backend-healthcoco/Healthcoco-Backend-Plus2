package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDrugDocument;

public interface SolrDrugRepository extends SolrCrudRepository<SolrDrugDocument, String> {
    @Query("drugName:*?0*")
    public List<SolrDrugDocument> find(String searchTerm);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND (discarded: ?1 OR discarded:false) AND (drugName:*?2* OR description:*?2* OR drugCode:*?2*)")
    public List<SolrDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND discarded:false AND (drugName:*?2* OR description:*?2* OR drugCode:*?2*)")
    public List<SolrDrugDocument> getCustomGlobalDrugs(Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, Pageable pageRequest);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:*?3* OR description:*?3* OR drugCode:*?3*)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:*?3* OR description:*?3* OR drugCode:*?3*))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:*?3* OR description:*?3* OR drugCode:*?3*)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:*?3* OR description:*?3* OR drugCode:*?3*))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:*?5* OR description:*?5* OR drugCode:*?5*)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:*?5* OR description:*?5* OR drugCode:*?5*))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:*?5* OR description:*?5* OR drugCode:*?5*)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:*?5* OR description:*?5* OR drugCode:*?5*))")
    public List<SolrDrugDocument> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Sort sort);

    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDrugDocument> getGlobalDrugs(Date date, boolean discarded, Pageable pageRequest);

    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded: false )")
    public List<SolrDrugDocument> getGlobalDrugs(Date date, boolean discarded, Sort sort);

    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND (drugName:*?2* OR description:*?2* OR drugCode:*?2*)")
    public List<SolrDrugDocument> getGlobalDrugs(Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND (drugName:*?2* OR description:*?2* OR drugCode:*?2*)")
    public List<SolrDrugDocument> getGlobalDrugs(Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:*?3* OR description:*?3* OR drugCode:*?3*)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (drugName:*?3* OR description:*?3* OR drugCode:*?3*)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:*?5* OR description:*?5* OR drugCode:*?5*)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND (drugName:*?5* OR description:*?5* OR drugCode:*?5*)")
    public List<SolrDrugDocument> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Sort sort);
}
