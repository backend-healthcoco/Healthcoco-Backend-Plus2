package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrComplaints;
import com.dpdocter.solr.document.SolrDiagnoses;
import com.dpdocter.solr.document.SolrDiagrams;
import com.dpdocter.solr.document.SolrInvestigations;
import com.dpdocter.solr.document.SolrNotes;
import com.dpdocter.solr.document.SolrObservations;

public interface SolrClinicalNotesService {

	boolean addComplaints(SolrComplaints request);

	boolean editComplaints(SolrComplaints request);

	boolean deleteComplaints(String id);

	List<SolrComplaints> searchComplaints(String searchTerm);

	boolean addDiagnoses(SolrDiagnoses request);

	boolean editDiagnoses(SolrDiagnoses request);

	boolean deleteDiagnoses(String id);

	List<SolrDiagnoses> searchDiagnoses(String searchTerm);

	boolean addNotes(SolrNotes request);

	boolean editNotes(SolrNotes request);

	boolean deleteNotes(String id);

	List<SolrNotes> searchNotes(String searchTerm);

	boolean addDiagrams(SolrDiagrams request);

	boolean editDiagrams(SolrDiagrams request);

	boolean deleteDiagrams(String id);

	List<SolrDiagrams> searchDiagrams(String searchTerm);

	boolean addInvestigations(SolrInvestigations request);

	boolean editInvestigations(SolrInvestigations request);

	boolean deleteInvestigations(String id);

	List<SolrInvestigations> searchInvestigations(String searchTerm);

	boolean addObservations(SolrObservations request);

	boolean editObservations(SolrObservations request);

	boolean deleteObservations(String id);

	List<SolrObservations> searchObservations(String searchTerm);

}
