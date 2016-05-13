package com.dpdocter.solr.document;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.enums.Currency;
import com.dpdocter.enums.DoctorExperienceUnit;
import com.dpdocter.enums.TimeUnit;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.beans.SolrWorkingSchedule;

@SolrDocument(solrCoreName = "doctors")
public class SolrDoctorDocument extends DoctorLocation {
    @Id
    @Field
    private String id;

    @Field
    private String userId;

    @Field
    private String firstName;

    @Field
    private String gender;

    @Field
    private String emailAddress;

    @Field
    private String mobileNumber;

    @Field
    private String imageUrl;

    @Transient
    private ConsultationFee consultationFee;

    @Field
    private int consultationFeeAmount;

    @Field
    private Currency consultationFeeCurrency;

    @Field
    private List<SolrWorkingSchedule> workingSchedules;

    @Field
    private List<String> specialities;

    @Transient
    private DoctorExperience experience;

    @Field
    private String experienceNum;

    @Field
    private DoctorExperienceUnit experiencePeriod;

    @Field
    private String facility;

    @Field
    private List<String> appointmentBookingNumber;

    @Transient
    private AppointmentSlot appointmentSlot;

    @Field
    private float appointmentSlotTime;

    @Field
    private TimeUnit appointmentSlotTimeUnit;

    @Field
    private Boolean isActive = false;

    @Field
    private Boolean isVerified = false;

    @Field
    private String coverImageUrl;

    @Field
    private String colorCode;

    @Field
    private String userState;

    @Field
    private String registerNumber;

    @Field
    private String days = "1";

    @Field
    private String months = "1";

    @Field
    private String years = "1";

    private DOB dob;

    @Transient
    private Double distance;

    @Field
    private String userUId;

    @Field
    private String timeZone = "IST";

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public ConsultationFee getConsultationFee() {
	this.consultationFee = new ConsultationFee(this.consultationFeeAmount, this.consultationFeeCurrency);
	return consultationFee;
    }

    public void setConsultationFee(ConsultationFee consultationFee) {
	this.consultationFee = consultationFee;
	if (consultationFee != null) {
	    this.consultationFeeAmount = consultationFee.getAmount();
	    this.consultationFeeCurrency = consultationFee.getCurrency();
	}
    }

    public List<SolrWorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<SolrWorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public List<String> getSpecialities() {
	return specialities;
    }

    public void setSpecialities(List<String> specialities) {
	this.specialities = specialities;
    }

    public DoctorExperience getExperience() {
	this.experience = new DoctorExperience(this.experienceNum, this.experiencePeriod);
	return experience;
    }

    public void setExperience(DoctorExperience experience) {
	this.experience = experience;
	if (experience != null) {
	    this.experienceNum = experience.getExperience();
	    this.experiencePeriod = experience.getPeriod();
	}
    }

    public String getFacility() {
	return facility;
    }

    public void setFacility(String facility) {
	this.facility = facility;
    }

    public Boolean getIsActive() {
	return isActive;
    }

    public void setIsActive(Boolean isActive) {
	this.isActive = isActive;
    }

    public Boolean getIsVerified() {
	return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
	this.isVerified = isVerified;
    }

    public String getCoverImageUrl() {
	return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
	this.coverImageUrl = coverImageUrl;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

    public String getUserState() {
	return userState;
    }

    public void setUserState(String userState) {
	this.userState = userState;
    }

    public String getRegisterNumber() {
	return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
	this.registerNumber = registerNumber;
    }

    public String getDays() {
	return days;
    }

    public void setDays(String days) {
	this.days = days;
    }

    public String getMonths() {
	return months;
    }

    public void setMonths(String months) {
	this.months = months;
    }

    public String getYears() {
	return years;
    }

    public void setYears(String years) {
	this.years = years;
    }

    public DOB getDob() {
	return new DOB(Integer.parseInt(days), Integer.parseInt(months), Integer.parseInt(years));
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public int getConsultationFeeAmount() {
	return consultationFeeAmount;
    }

    public void setConsultationFeeAmount(int consultationFeeAmount) {
	this.consultationFeeAmount = consultationFeeAmount;
	if (this.consultationFee != null)
	    this.consultationFee.setAmount(consultationFeeAmount);
	else
	    this.consultationFee = new ConsultationFee(this.consultationFeeAmount, this.consultationFeeCurrency);
    }

    public Currency getConsultationFeeCurrency() {
	return consultationFeeCurrency;
    }

    public void setConsultationFeeCurrency(Currency consultationFeeCurrency) {
	this.consultationFeeCurrency = consultationFeeCurrency;
	if (this.consultationFee != null)
	    this.consultationFee.setCurrency(consultationFeeCurrency);
	else
	    this.consultationFee = new ConsultationFee(this.consultationFeeAmount, this.consultationFeeCurrency);
    }

    public String getExperienceNum() {
	return experienceNum;
    }

    public void setExperienceNum(String experienceNum) {
	this.experienceNum = experienceNum;
	if (this.experience != null)
	    this.experience.setExperience(experienceNum);
	else
	    this.experience = new DoctorExperience(this.experienceNum, this.experiencePeriod);
    }

    public DoctorExperienceUnit getExperiencePeriod() {
	return experiencePeriod;
    }

    public void setExperiencePeriod(DoctorExperienceUnit experiencePeriod) {
	this.experiencePeriod = experiencePeriod;
	if (this.experience != null)
	    this.experience.setPeriod(experiencePeriod);
	else
	    this.experience = new DoctorExperience(this.experienceNum, this.experiencePeriod);
    }

    public Double getDistance() {
	return distance;
    }

    public void setDistance(Double distance) {
	this.distance = distance;
    }

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
		if (appointmentSlot != null) {
		    this.appointmentSlotTime = appointmentSlot.getTime();
		    this.appointmentSlotTimeUnit = appointmentSlot.getTimeUnit();
		}
	}

	public float getAppointmentSlotTime() {
		return appointmentSlotTime;
	}

	public void setAppointmentSlotTime(float appointmentSlotTime) {
		this.appointmentSlotTime = appointmentSlotTime;
		if (this.appointmentSlot != null)
		    this.appointmentSlot.setTime(appointmentSlotTime);
		else
		    this.appointmentSlot = new AppointmentSlot(this.appointmentSlotTime, this.appointmentSlotTimeUnit);
	}

	public TimeUnit getAppointmentSlotTimeUnit() {
		return appointmentSlotTimeUnit;
	}

	public void setAppointmentSlotTimeUnit(TimeUnit appointmentSlotTimeUnit) {
		this.appointmentSlotTimeUnit = appointmentSlotTimeUnit;
		if (this.appointmentSlot != null)
		    this.appointmentSlot.setTimeUnit(appointmentSlotTimeUnit);
		else
		    this.appointmentSlot = new AppointmentSlot(this.appointmentSlotTime, this.appointmentSlotTimeUnit);
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public String toString() {
		return "SolrDoctorDocument [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", gender=" + gender
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl
				+ ", consultationFee=" + consultationFee + ", consultationFeeAmount=" + consultationFeeAmount
				+ ", consultationFeeCurrency=" + consultationFeeCurrency + ", workingSchedules=" + workingSchedules
				+ ", specialities=" + specialities + ", experience=" + experience + ", experienceNum=" + experienceNum
				+ ", experiencePeriod=" + experiencePeriod + ", facility=" + facility + ", appointmentBookingNumber="
				+ appointmentBookingNumber + ", appointmentSlot=" + appointmentSlot + ", appointmentSlotTime="
				+ appointmentSlotTime + ", appointmentSlotTimeUnit=" + appointmentSlotTimeUnit + ", isActive="
				+ isActive + ", isVerified=" + isVerified + ", coverImageUrl=" + coverImageUrl + ", colorCode="
				+ colorCode + ", userState=" + userState + ", registerNumber=" + registerNumber + ", days=" + days
				+ ", months=" + months + ", years=" + years + ", dob=" + dob + ", distance=" + distance + ", userUId="
				+ userUId + ", timeZone=" + timeZone + "]";
	}
}
