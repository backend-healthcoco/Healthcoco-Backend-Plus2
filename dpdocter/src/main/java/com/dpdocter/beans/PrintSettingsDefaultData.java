package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PrintSettingsDefaultData {

	private String id;

	private List<String> layout;

	private List<String> color;

	private List<String> margins;

	private List<String> pageSize;

	private List<String> logoType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getLayout() {
		return layout;
	}

	public void setLayout(List<String> layout) {
		this.layout = layout;
	}

	public List<String> getColor() {
		return color;
	}

	public void setColor(List<String> color) {
		this.color = color;
	}

	public List<String> getMargins() {
		return margins;
	}

	public void setMargins(List<String> margins) {
		this.margins = margins;
	}

	public List<String> getPageSize() {
		return pageSize;
	}

	public void setPageSize(List<String> pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getLogoType() {
		return logoType;
	}

	public void setLogoType(List<String> logoType) {
		this.logoType = logoType;
	}

	@Override
	public String toString() {
		return "PrintSettingsData [id=" + id + ", layout=" + layout + ", color=" + color + ", margins=" + margins
				+ ", pageSize=" + pageSize + ", logoType=" + logoType + "]";
	}
}
