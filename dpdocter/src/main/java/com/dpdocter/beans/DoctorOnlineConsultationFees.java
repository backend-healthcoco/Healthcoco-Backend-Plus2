package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;


public class DoctorOnlineConsultationFees extends GenericCollection{

	private String id;

    private String doctorId;
    
	private List<DoctorConsultation>consultationType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<DoctorConsultation> getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(List<DoctorConsultation> consultationType) {
		this.consultationType = consultationType;
	}



	
	


}
