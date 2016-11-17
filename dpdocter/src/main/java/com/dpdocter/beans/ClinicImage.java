package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicImage {

    private String imageUrl;

    private String thumbnailUrl;

    private int counter;

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public int getCounter() {
	return counter;
    }

    public void setCounter(int counter) {
	this.counter = counter;
    }

    public String getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
	return "{imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", counter=" + counter + "}";
    }

}
