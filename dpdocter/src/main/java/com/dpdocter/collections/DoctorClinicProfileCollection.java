package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.DoctorFacility;

@Document(collection = "doctor_clinic_profile_cl")
public class DoctorClinicProfileCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String locationId;

    @Field
    private String patientInitial = "P";

    @Field
    private int patientCounter = 0;

    @Field
    private List<String> appointmentBookingNumber;

    @Field
    private ConsultationFee consultationFee;

    @Field
    private AppointmentSlot appointmentSlot;

    @Field
    private List<WorkingSchedule> workingSchedules;

    @Field
    private DoctorFacility facility = DoctorFacility.BOOK;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getPatientInitial() {
	return patientInitial;
    }

    public void setPatientInitial(String patientInitial) {
	this.patientInitial = patientInitial;
    }

    public int getPatientCounter() {
	return patientCounter;
    }

    public void setPatientCounter(int patientCounter) {
	this.patientCounter = patientCounter;
    }

    public List<String> getAppointmentBookingNumber() {
	return appointmentBookingNumber;
    }

    public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
	this.appointmentBookingNumber = appointmentBookingNumber;
    }

    public ConsultationFee getConsultationFee() {
	return consultationFee;
    }

    public void setConsultationFee(ConsultationFee consultationFee) {
	this.consultationFee = consultationFee;
    }

    public AppointmentSlot getAppointmentSlot() {
	return appointmentSlot;
    }

    public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
	this.appointmentSlot = appointmentSlot;
    }

    public List<WorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

    public DoctorFacility getFacility() {
	return facility;
    }

    public void setFacility(DoctorFacility facility) {
	this.facility = facility;
    }

    @Override
    public String toString() {
	return "DoctorClinicProfileCollection [id=" + id + ", locationId=" + locationId + ", patientInitial=" + patientInitial + ", patientCounter="
		+ patientCounter + ", appointmentBookingNumber=" + appointmentBookingNumber + ", consultationFee=" + consultationFee + ", appointmentSlot="
		+ appointmentSlot + ", workingSchedules=" + workingSchedules + ", facility=" + facility + "]";
    }
}
