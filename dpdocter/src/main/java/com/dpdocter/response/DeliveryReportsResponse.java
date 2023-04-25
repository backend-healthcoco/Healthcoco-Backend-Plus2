package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DeliveryReports;

public class DeliveryReportsResponse {

	List<DeliveryReports> deliveryReports;
	Integer count;

	public List<DeliveryReports> getDeliveryReports() {
		return deliveryReports;
	}

	public void setDeliveryReports(List<DeliveryReports> deliveryReports) {
		this.deliveryReports = deliveryReports;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "DeliveryReportsResponse [deliveryReports=" + deliveryReports + ", count=" + count + "]";
	}

}
