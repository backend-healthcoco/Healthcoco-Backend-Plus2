package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.QueueStatus;

@Document(collection = "patient_queue_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class PatientQueueCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Indexed
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId patientId;

    @Indexed
    private Date date;

    @Field
    private Integer startTime;

    @Field
    private Integer sequenceNo;

    @Field
    private String appointmentId;

    @Field
    private Boolean discarded = false;

    @Field
    private QueueStatus status = QueueStatus.SCHEDULED;
    
    @Field
    private Integer waitingTime = 0;//in seconds
    
    @Field
    private Integer engagedTime = 0;//in seconds
    
    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
	this.hospitalId = hospitalId;
    }

    public ObjectId getPatientId() {
	return patientId;
    }

    public void setPatientId(ObjectId patientId) {
	this.patientId = patientId;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public Integer getSequenceNo() {
	return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
	this.sequenceNo = sequenceNo;
    }

    public Integer getStartTime() {
	return startTime;
    }

    public void setStartTime(Integer startTime) {
	this.startTime = startTime;
    }

    public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
	}

	public Integer getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(Integer waitingTime) {
		this.waitingTime = waitingTime;
	}

	public Integer getEngagedTime() {
		return engagedTime;
	}

	public void setEngagedTime(Integer engagedTime) {
		this.engagedTime = engagedTime;
	}

	@Override
	public String toString() {
		return "PatientQueueCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", date=" + date + ", startTime="
				+ startTime + ", sequenceNo=" + sequenceNo + ", appointmentId=" + appointmentId + ", discarded="
				+ discarded + ", status=" + status + ", waitingTime=" + waitingTime + ", engagedTime=" + engagedTime + "]";
	}
}
