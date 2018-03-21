package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.CalenderJasperBean;

public class CalenderJasperBeanList {

	private String doctor;

	private List<CalenderJasperBean> calenders;

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public List<CalenderJasperBean> getCalenders() {
		return calenders;
	}

	public void setCalenders(List<CalenderJasperBean> calenders) {
		this.calenders = calenders;
	}

}
