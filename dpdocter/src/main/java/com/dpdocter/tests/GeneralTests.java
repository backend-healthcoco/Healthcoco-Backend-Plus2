package com.dpdocter.tests;

import java.util.ArrayList;
import java.util.List;

import com.dpdocter.solr.document.SolrSpecialityDocument;

public class GeneralTests {
    public static void main(String args[]) {
	String[] specialities = { "Cold", "Cough", "Fever" };
	List<SolrSpecialityDocument> solrSpecialityDocuments = new ArrayList<SolrSpecialityDocument>();
	for (String speciality : specialities) {
	    SolrSpecialityDocument solrSpecialityDocument = new SolrSpecialityDocument();
	    solrSpecialityDocument.setSpeciality(speciality);
	    solrSpecialityDocuments.add(solrSpecialityDocument);
	}
	System.out.println(Converter.ObjectToJSON(solrSpecialityDocuments));
    }
}
