package com.dpdocter.beans;


/**
 * @author veeraj
 */

public class Doctor {
    private String id;

    private String firstName;

    private String lastName;
    
    private String mobileNumber;
    
    private String emailAddress;
    
    private String imageUrl;

    private String specialization;

    private DoctorClinicProfile doctorClinicProfile;
    
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public DoctorClinicProfile getDoctorClinicProfile() {
		return doctorClinicProfile;
	}

	public void setDoctorClinicProfile(DoctorClinicProfile doctorClinicProfile) {
		this.doctorClinicProfile = doctorClinicProfile;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", mobileNumber="
				+ mobileNumber + ", emailAddress=" + emailAddress + ", imageUrl=" + imageUrl + ", specialization="
				+ specialization + ", doctorClinicProfile=" + doctorClinicProfile + "]";
	}

}
