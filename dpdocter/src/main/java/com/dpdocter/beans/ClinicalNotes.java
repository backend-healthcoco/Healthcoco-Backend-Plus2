package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotes extends GenericCollection {

    private String id;

    private String uniqueEmrId;

    private List<Diagnoses> diagnoses;

    private List<Diagram> diagrams;

    private String note;

    private String observation;

    private String investigation;

    private String diagnosis;

    private String complaint;
    
    private String doctorId;

    private String locationId;

    private String hospitalId;

    private boolean inHistory = false;

    private Boolean discarded = false;

    private String visitId;

    private String patientId;

    private VitalSigns vitalSigns;
    
    private String appointmentId;
    
    private WorkingHours time;
    
    private Date fromDate;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<Diagnoses> getDiagnoses() {
	return diagnoses;
    }

    public void setDiagnoses(List<Diagnoses> diagnoses) {
	this.diagnoses = diagnoses;
    }

    public List<Diagram> getDiagrams() {
	return diagrams;
    }

    public void setDiagrams(List<Diagram> diagrams) {
	this.diagrams = diagrams;
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

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public VitalSigns getVitalSigns() {
	return vitalSigns;
    }

    public void setVitalSigns(VitalSigns vitalSigns) {
	this.vitalSigns = vitalSigns;
    }

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	@Override
	public String toString() {
		return "ClinicalNotes [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", diagnoses=" + diagnoses
				+ ", diagrams=" + diagrams + ", note=" + note + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnosis=" + diagnosis + ", complaint=" + complaint
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", inHistory=" + inHistory + ", discarded=" + discarded + ", visitId=" + visitId + ", patientId="
				+ patientId + ", vitalSigns=" + vitalSigns + ", appointmentId=" + appointmentId + ", time=" + time
				+ ", fromDate=" + fromDate + "]";
	}
}
