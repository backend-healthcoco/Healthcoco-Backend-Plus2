package com.dpdocter.beans;

public class ClinicImage {

    private String imageUrl;

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

    @Override
    public String toString() {
	return "ClinicImage [imageUrl=" + imageUrl + ", counter=" + counter + "]";
    }
}
