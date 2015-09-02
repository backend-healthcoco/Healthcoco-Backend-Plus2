package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.WorkingSchedule;

@Document(collection = "doctor_clinic_profile_cl")
public class DoctorClinicProfileCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String userLocationId;

    @Field
    private List<String> appointmentBookingNumber;

    @Field
    private ConsultationFee consultationFee;

    @Field
    private AppointmentSlot appointmentSlot;

    @Field
    private List<WorkingSchedule> workingSchedules;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUserLocationId() {
	return userLocationId;
    }

    public void setUserLocationId(String userLocationId) {
	this.userLocationId = userLocationId;
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

    @Override
    public String toString() {
	return "DoctorClinicProfileCollection [id=" + id + ", userLocationId=" + userLocationId + ", appointmentBookingNumber=" + appointmentBookingNumber
		+ ", consultationFee=" + consultationFee + ", appointmentSlot=" + appointmentSlot + ", workingSchedules=" + workingSchedules + "]";
    }

}
