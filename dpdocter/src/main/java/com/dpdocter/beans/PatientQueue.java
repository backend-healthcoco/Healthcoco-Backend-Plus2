package com.dpdocter.beans;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.QueueStatus;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatientQueue {

	private String id;
    
	private Integer count; 
    
    private QueueStatus status = QueueStatus.SCHEDULED;
    
    private String doctorId;

    private String locationId;

    private String hospitalId;

    private PatientCard patient;

    private Date date;

    private String sequenceNo;

    private String appointmentId;

    private long waitedFor = 0;
    
    private long engagedAt = 0;
    
    private long checkedInAt = 0;
    
    private long checkedOutAt = 0;
    
    private long engagedFor = 0;
    
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

    public PatientCard getPatient() {
	return patient;
    }

    public void setPatient(PatientCard patient) {
	this.patient = patient;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getSequenceNo() {
	return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
	this.sequenceNo = sequenceNo;
    }

    public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
	}

	public long getWaitedFor() {
		return waitedFor;
	}

	public void setWaitedFor(long waitedFor) {
		this.waitedFor = waitedFor;
	}

	public long getEngagedAt() {
		return engagedAt;
	}

	public void setEngagedAt(long engagedAt) {
		this.engagedAt = engagedAt;
	}

	public long getCheckedInAt() {
		return checkedInAt;
	}

	public void setCheckedInAt(long checkedInAt) {
		this.checkedInAt = checkedInAt;
	}

	public long getCheckedOutAt() {
		return checkedOutAt;
	}

	public void setCheckedOutAt(long checkedOutAt) {
		this.checkedOutAt = checkedOutAt;
	}

	public long getEngagedFor() {
		return engagedFor;
	}

	public void setEngagedFor(long engagedFor) {
		this.engagedFor = engagedFor;
	}

	@Override
	public String toString() {
		return "PatientQueue [id=" + id + ", count=" + count + ", status=" + status + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patient=" + patient + ", date="
				+ date + ", sequenceNo=" + sequenceNo + ", appointmentId=" + appointmentId + ", waitedFor=" + waitedFor
				+ ", engagedAt=" + engagedAt + ", checkedInAt=" + checkedInAt + ", checkedOutAt=" + checkedOutAt
				+ ", engagedFor=" + engagedFor + "]";
	}
}
