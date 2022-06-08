package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.v2.PatientTreatment;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.QueueStatus;

public class AppointmentRequest {

	private String appointmentId;

	private AppointmentState state;

	private String explanation;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private WorkingHours time;

	private Date fromDate;

	private Date toDate;

	private AppointmentCreatedBy createdBy;

	private Boolean notifyPatientBySms;

	private Boolean notifyPatientByEmail;

	private Boolean notifyDoctorBySms;

	private Boolean notifyDoctorByEmail;

	private String cancelledBy;

	private String localPatientName;

	private String mobileNumber;

	private String visitId;

	private QueueStatus status = QueueStatus.SCHEDULED;

	private long waitedFor = 0;

	private long engagedFor = 0;

	private long engagedAt = 0;

	private long checkedInAt = 0;

	private long checkedOutAt = 0;

	private String category;

	private String branch;

	private String cancelledByProfile;

	private String gender;

	private DOB dob;

	private Integer age;

	private Boolean isChild = false;

	private List<Fields> treatmentFields;

	private PatientTreatmentAddEditRequest patientTreatments;

	private String PNUM;

	private Boolean isCreatedByPatient = false;

	private Boolean isTreatmentEdited = false;
	
	private AppointmentType type=AppointmentType.APPOINTMENT;
	
	private ConsultationType consultationType;
	
	private String problemDetailsId;
	
private AppointmentSlot onlineConsultationSlot;
	
private Long callDurationInMinutes=0L;

private Boolean isDentalChainAppointment = false;


	public Boolean getIsTreatmentEdited() {
		return isTreatmentEdited;
	}

	public void setIsTreatmentEdited(Boolean isTreatmentEdited) {
		this.isTreatmentEdited = isTreatmentEdited;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public AppointmentCreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AppointmentCreatedBy createdBy) {
		this.createdBy = createdBy;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCancelledByProfile() {
		return cancelledByProfile;
	}

	public void setCancelledByProfile(String cancelledByProfile) {
		this.cancelledByProfile = cancelledByProfile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
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

	public PatientTreatmentAddEditRequest getPatientTreatments() {
		return patientTreatments;
	}

	public void setPatientTreatments(PatientTreatmentAddEditRequest patientTreatments) {
		this.patientTreatments = patientTreatments;
	}
	
	

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}
	

	public Boolean getIsDentalChainAppointment() {
		return isDentalChainAppointment;
	}

	public void setIsDentalChainAppointment(Boolean isDentalChainAppointment) {
		this.isDentalChainAppointment = isDentalChainAppointment;
	}

	public AppointmentSlot getOnlineConsultationSlot() {
		return onlineConsultationSlot;
	}

	public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
		this.onlineConsultationSlot = onlineConsultationSlot;
	}

	
	
	
	public Long getCallDurationInMinutes() {
		return callDurationInMinutes;
	}

	public void setCallDurationInMinutes(Long callDurationInMinutes) {
		this.callDurationInMinutes = callDurationInMinutes;
	}

	public ConsultationType getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(ConsultationType consultationType) {
		this.consultationType = consultationType;
	}

	public String getProblemDetailsId() {
		return problemDetailsId;
	}

	public void setProblemDetailsId(String problemDetailsId) {
		this.problemDetailsId = problemDetailsId;
	}

	@Override
	public String toString() {
		return "AppointmentRequest [appointmentId=" + appointmentId + ", state=" + state + ", explanation="
				+ explanation + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", time=" + time + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", createdBy=" + createdBy + ", notifyPatientBySms=" + notifyPatientBySms + ", notifyPatientByEmail="
				+ notifyPatientByEmail + ", notifyDoctorBySms=" + notifyDoctorBySms + ", notifyDoctorByEmail="
				+ notifyDoctorByEmail + ", cancelledBy=" + cancelledBy + ", localPatientName=" + localPatientName
				+ ", mobileNumber=" + mobileNumber + ", visitId=" + visitId + ", status=" + status + ", waitedFor="
				+ waitedFor + ", engagedFor=" + engagedFor + ", engagedAt=" + engagedAt + ", checkedInAt=" + checkedInAt
				+ ", checkedOutAt=" + checkedOutAt + ", category=" + category + ", branch=" + branch
				+ ", cancelledByProfile=" + cancelledByProfile + ", gender=" + gender + ", dob=" + dob + ", age=" + age
				+ ", isChild=" + isChild + ", treatmentFields=" + treatmentFields + ", PNUM=" + PNUM
				+ ", isCreatedByPatient=" + isCreatedByPatient + "]";
	}

}
