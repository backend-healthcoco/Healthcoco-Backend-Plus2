package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "symptoms")
public class SolrSymptomsDocument {

		@Id
	    @Field
	    private String id;

	    @Field
	    private String specialityId;
	    
	    @Field
	    private String symptom;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSpecialityId() {
			return specialityId;
		}

		public void setSpecialityId(String specialityId) {
			this.specialityId = specialityId;
		}

		public String getSymptom() {
			return symptom;
		}

		public void setSymptom(String symptom) {
			this.symptom = symptom;
		}

		@Override
		public String toString() {
			return "SolrSymptomsDocument [id=" + id + ", specialityId=" + specialityId + ", symptom=" + symptom + "]";
		}
}
