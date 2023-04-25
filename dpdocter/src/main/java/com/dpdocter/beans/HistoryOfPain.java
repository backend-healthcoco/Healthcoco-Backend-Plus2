package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.Nature;
import com.dpdocter.enums.PainType;

public class HistoryOfPain {

	private String site;
	private Nature nature;
	private List<PainType> type;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Nature getNature() {
		return nature;
	}

	public void setNature(Nature nature) {
		this.nature = nature;
	}

	public List<PainType> getType() {
		return type;
	}

	public void setType(List<PainType> type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "HistoryOfPain [site=" + site + ", nature=" + nature + ", type=" + type + "]";
	}

}
