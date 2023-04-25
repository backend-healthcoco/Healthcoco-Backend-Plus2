package com.dpdocter.response;

public class ImageURLResponse {

	private String imageUrl;

	private String thumbnailUrl;

	private String name;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ImageURLResponse [imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + "]";
	}
}
