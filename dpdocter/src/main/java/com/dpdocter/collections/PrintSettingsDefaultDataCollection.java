package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class PrintSettingsDefaultDataCollection {

    @Id
    private String id;

    @Field
    private List<String> layout;

    @Field
    private List<String> color;

    @Field
    private List<String> margins;

    @Field
    private List<String> pageSize;

    @Field
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
	return "PrintSettingsDefaultDataCollection [id=" + id + ", layout=" + layout + ", color=" + color + ", margins=" + margins + ", pageSize=" + pageSize
		+ ", logoType=" + logoType + "]";
    }
}
