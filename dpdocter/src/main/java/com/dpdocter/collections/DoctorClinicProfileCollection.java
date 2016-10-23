package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.DoctorFacility;

@Document(collection = "doctor_clinic_profile_cl")
public class DoctorClinicProfileCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userLocationId;

	@Field
	private String patientInitial = "P";
	
	@Field
	private int patientCounter = 1;

    @Field
    private List<String> appointmentBookingNumber;

    @Field
    private ConsultationFee consultationFee;

    @Field
    private AppointmentSlot appointmentSlot = new AppointmentSlot();

    @Field
    private List<WorkingSchedule> workingSchedules;

    @Field
    private DoctorFacility facility = DoctorFacility.CALL;

    @Field
    private Integer noOfReviews = 0;

    @Field
    private Integer noOfRecommenations = 0;

    @Field
    private String timeZone = "IST";

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getUserLocationId() {
	return userLocationId;
    }

    public void setUserLocationId(ObjectId userLocationId) {
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

    public DoctorFacility getFacility() {
	return facility;
    }

    public void setFacility(DoctorFacility facility) {
	this.facility = facility;
    }

    public Integer getNoOfReviews() {
	return noOfReviews;
    }

    public void setNoOfReviews(Integer noOfReviews) {
	this.noOfReviews = noOfReviews;
    }

    public Integer getNoOfRecommenations() {
	return noOfRecommenations;
    }

    public void setNoOfRecommenations(Integer noOfRecommenations) {
	this.noOfRecommenations = noOfRecommenations;
    }

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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

	@Override
	public String toString() {
		return "DoctorClinicProfileCollection [id=" + id + ", userLocationId=" + userLocationId + ", patientInitial="
				+ patientInitial + ", patientCounter=" + patientCounter + ", appointmentBookingNumber="
				+ appointmentBookingNumber + ", consultationFee=" + consultationFee + ", appointmentSlot="
				+ appointmentSlot + ", workingSchedules=" + workingSchedules + ", facility=" + facility
				+ ", noOfReviews=" + noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", timeZone="
				+ timeZone + "]";
	}
}
