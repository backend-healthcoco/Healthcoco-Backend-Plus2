package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PeriodEnums;

public class Country  extends GenericCollection{

	private String id;

	private String countryName;

	private String countryCode;

	private PeriodEnums period;

	private List<Tax> taxValue;

	private Boolean isDiscarded = Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public PeriodEnums getPeriod() {
		return period;
	}

	public void setPeriod(PeriodEnums period) {
		this.period = period;
	}

	public List<Tax> getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(List<Tax> taxValue) {
		this.taxValue = taxValue;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	@Override
	public String toString() {
		return "Country [id=" + id + ", countryName=" + countryName + ", countryCode=" + countryCode + ", period="
				+ period + ", taxValue=" + taxValue + ", isDiscarded=" + isDiscarded + "]";
	}
	
	

}
