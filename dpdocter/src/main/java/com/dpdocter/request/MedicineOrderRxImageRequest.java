package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.MedicineOrderImages;

public class MedicineOrderRxImageRequest {

	private String id;
	private List<MedicineOrderImages> rxImage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<MedicineOrderImages> getRxImage() {
		return rxImage;
	}

	public void setRxImage(List<MedicineOrderImages> rxImage) {
		this.rxImage = rxImage;
	}

	@Override
	public String toString() {
		return "MedicineOrderRxImageRequest [id=" + id + ", rxImage=" + rxImage + "]";
	}

}
