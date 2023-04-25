package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Drug;

public class DrugsAnalyticsData {

	private List<Drug> drugs;

	private Date date;

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "DrugsAnalyticsData [drugs=" + drugs + ", date=" + date + "]";
	}

}
