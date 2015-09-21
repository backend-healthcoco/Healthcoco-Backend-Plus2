package com.dpdocter.beans;

public class PageSetup {

	private String layout;
	
	private String color;
	
	private String margins;
	
	private String pageSize;

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getMargins() {
		return margins;
	}

	public void setMargins(String margins) {
		this.margins = margins;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "PageSetup [layout=" + layout + ", color=" + color + ", margins=" + margins + ", pageSize=" + pageSize
				+ "]";
	}	
}
