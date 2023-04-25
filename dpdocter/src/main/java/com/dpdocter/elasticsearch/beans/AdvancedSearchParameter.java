package com.dpdocter.elasticsearch.beans;

import com.dpdocter.enums.AdvancedSearchType;

public class AdvancedSearchParameter {
	private AdvancedSearchType searchType;

	private String searchValue;

	public AdvancedSearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(AdvancedSearchType searchType) {
		this.searchType = searchType;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	@Override
	public String toString() {
		return "AdvancedSearchParameter [searchType=" + searchType + ", searchValue=" + searchValue + "]";
	}

}
