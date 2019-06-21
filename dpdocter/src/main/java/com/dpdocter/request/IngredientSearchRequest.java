package com.dpdocter.request;

import java.util.List;

public class IngredientSearchRequest {
	private int size;
	private int page;
	private boolean discarded;
	private String searchTerm;
	private List<String> nutrients;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public List<String> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<String> nutrients) {
		this.nutrients = nutrients;
	}

}
