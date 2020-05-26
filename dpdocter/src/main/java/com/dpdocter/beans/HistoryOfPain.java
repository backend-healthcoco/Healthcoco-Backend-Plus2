package com.dpdocter.beans;

import com.dpdocter.enums.Nature;
import com.dpdocter.enums.PainType;

public class HistoryOfPain {

	private String site;
	private Nature nature;
	private PainType type;
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
	public PainType getType() {
		return type;
	}
	public void setType(PainType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "HistoryOfPain [site=" + site + ", nature=" + nature + ", type=" + type + "]";
	}
	
		
	
}
