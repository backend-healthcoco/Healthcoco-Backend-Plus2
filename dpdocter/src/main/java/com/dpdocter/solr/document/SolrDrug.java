package com.dpdocter.solr.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "drugs")
public class SolrDrug {
	@Id
	@Field
	private String id;

	@Field
	private String drugType;

	@Field
	private String drugName;

	@Field
	private String description;

	@Field
	private List<String> genericNames;

	@Field
	private String drugCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<String> genericNames) {
		this.genericNames = genericNames;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	@Override
	public String toString() {
		return "SolrDrug [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", description=" + description + ", genericNames=" + genericNames
				+ ", drugCode=" + drugCode + "]";
	}

}
