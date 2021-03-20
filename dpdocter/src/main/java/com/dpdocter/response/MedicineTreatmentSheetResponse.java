package com.dpdocter.response;

import java.util.List;
import java.util.Date;
import com.dpdocter.beans.MedicineSheet;
import com.dpdocter.collections.GenericCollection;

public class MedicineTreatmentSheetResponse extends GenericCollection{

	private String id;
	private String doctorId;
	private String patientId;
	private String locationId;
	private String hospitalId;
	private Date Date;
    private List<MedicineSheet> medicine;
    private Boolean discarded;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Date getDate() {
		return Date;
	}
	public void setDate(Date date) {
		Date = date;
	}
	public List<MedicineSheet> getMedicine() {
		return medicine;
	}
	public void setMedicine(List<MedicineSheet> medicine) {
		this.medicine = medicine;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	
	
	
}
