package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Tax;
import com.dpdocter.enums.PeriodEnums;

public class CountryCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private String countryName;

	@Field
	private String countryCode;

	@Field
	private PeriodEnums period;

	@Field
	private List<Tax> taxValue;

	@Field
	private Boolean isDiscarded = Boolean.FALSE;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
	
	
}
