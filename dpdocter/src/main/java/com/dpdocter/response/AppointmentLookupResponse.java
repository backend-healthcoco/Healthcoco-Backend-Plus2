package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Fields;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.User;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.QueueStatus;

public class AppointmentLookupResponse extends GenericCollection {

	private String id;
    
    private String subject;
    
    private String explanation;

    private String doctorId;
    
    private String locationId;
    
    private String hospitalId;
    
    private String appointmentId;
    
    private WorkingHours time;

    private String patientId;
    
    private AppointmentState state = AppointmentState.NEW;
    
    private AppointmentType type = AppointmentType.APPOINTMENT;
    
    private Boolean isRescheduled = false;
    
    private Date fromDate;

    private Date toDate;
        
    private Boolean isCalenderBlocked = false;
    
    private Boolean isFeedbackAvailable = false;

    private Boolean isAllDayEvent = false;
    
    private String cancelledBy;
        
    private Boolean notifyPatientBySms;
    
    private Boolean notifyPatientByEmail;
    
    private Boolean notifyDoctorBySms;

    private Boolean notifyDoctorByEmail;
    
    private String visitId;
    
    private User doctor;
    
    private Location location;
    
    private User patient;
	
    private PatientCard patientCard;
    
    private QueueStatus status = QueueStatus.SCHEDULED;
    
    private long waitedFor = 0;
    
    private long engagedFor = 0;
    
    private long engagedAt = 0;
    
    private long checkedInAt = 0;
    
    private long checkedOutAt = 0;

    private Integer count; 
    
    private String category;
    
    private String branch;
    
    private String cancelledByProfile;
    
    private String localPatientName;
    
	private List<Fields> treatmentFields;

	private Boolean isCreatedByPatient = false;

	private ConsultationType consultationType;
	
	private Date consultationStartedOn;
	
	private String problemDetailsId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	public Boolean getIsRescheduled() {
		return isRescheduled;
	}

	public void setIsRescheduled(Boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Boolean getIsCalenderBlocked() {
		return isCalenderBlocked;
	}

	public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
		this.isCalenderBlocked = isCalenderBlocked;
	}

	public Boolean getIsFeedbackAvailable() {
		return isFeedbackAvailable;
	}

	public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
		this.isFeedbackAvailable = isFeedbackAvailable;
	}

	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public Boolean getNotifyPatientBySms() {
		return notifyPatientBySms;
	}

	public void setNotifyPatientBySms(Boolean notifyPatientBySms) {
		this.notifyPatientBySms = notifyPatientBySms;
	}

	public Boolean getNotifyPatientByEmail() {
		return notifyPatientByEmail;
	}

	public void setNotifyPatientByEmail(Boolean notifyPatientByEmail) {
		this.notifyPatientByEmail = notifyPatientByEmail;
	}

	public Boolean getNotifyDoctorBySms() {
		return notifyDoctorBySms;
	}

	public void setNotifyDoctorBySms(Boolean notifyDoctorBySms) {
		this.notifyDoctorBySms = notifyDoctorBySms;
	}

	public Boolean getNotifyDoctorByEmail() {
		return notifyDoctorByEmail;
	}

	public void setNotifyDoctorByEmail(Boolean notifyDoctorByEmail) {
		this.notifyDoctorByEmail = notifyDoctorByEmail;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public User getPatient() {
		return patient;
	}

	public void setPatient(User patient) {
		this.patient = patient;
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

	public long getEngagedFor() {
		return engagedFor;
	}

	public void setEngagedFor(long engagedFor) {
		this.engagedFor = engagedFor;
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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public PatientCard getPatientCard() {
		return patientCard;
	}

	public void setPatientCard(PatientCard patientCard) {
		this.patientCard = patientCard;
	}

	public String getCancelledByProfile() {
		return cancelledByProfile;
	}

	public void setCancelledByProfile(String cancelledByProfile) {
		this.cancelledByProfile = cancelledByProfile;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public Boolean getIsCreatedByPatient() {
		return isCreatedByPatient;
	}

	public void setIsCreatedByPatient(Boolean isCreatedByPatient) {
		this.isCreatedByPatient = isCreatedByPatient;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public ConsultationType getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(ConsultationType consultationType) {
		this.consultationType = consultationType;
	}

	public Date getConsultationStartedOn() {
		return consultationStartedOn;
	}

	public void setConsultationStartedOn(Date consultationStartedOn) {
		this.consultationStartedOn = consultationStartedOn;
	}
	
	

	public String getProblemDetailsId() {
		return problemDetailsId;
	}

	public void setProblemDetailsId(String problemDetailsId) {
		this.problemDetailsId = problemDetailsId;
	}

	@Override
	public String toString() {
		return "AppointmentLookupResponse [id=" + id + ", subject=" + subject + ", explanation=" + explanation
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", patientId=" + patientId + ", state="
				+ state + ", type=" + type + ", isRescheduled=" + isRescheduled + ", fromDate=" + fromDate + ", toDate="
				+ toDate + ", isCalenderBlocked=" + isCalenderBlocked + ", isFeedbackAvailable=" + isFeedbackAvailable
				+ ", isAllDayEvent=" + isAllDayEvent + ", cancelledBy=" + cancelledBy + ", notifyPatientBySms="
				+ notifyPatientBySms + ", notifyPatientByEmail=" + notifyPatientByEmail + ", notifyDoctorBySms="
				+ notifyDoctorBySms + ", notifyDoctorByEmail=" + notifyDoctorByEmail + ", visitId=" + visitId
				+ ", doctor=" + doctor + ", location=" + location + ", patient=" + patient + ", patientCard="
				+ patientCard + ", status=" + status + ", waitedFor=" + waitedFor + ", engagedFor=" + engagedFor
				+ ", engagedAt=" + engagedAt + ", checkedInAt=" + checkedInAt + ", checkedOutAt=" + checkedOutAt
				+ ", count=" + count + ", category=" + category + ", branch=" + branch + ", cancelledByProfile="
				+ cancelledByProfile + ", localPatientName=" + localPatientName + ", treatmentFields=" + treatmentFields
				+ ", isCreatedByPatient=" + isCreatedByPatient + ", consultationType=" + consultationType
				+ ", consultationStartedOn=" + consultationStartedOn + "]";
	}
}
