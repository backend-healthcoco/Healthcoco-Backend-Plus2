package com.dpdocter.response;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

public class DentalWorksReceiptResponse extends GenericCollection {

	private String id;

	private String uniqueReceiptId;

	private ReceiptType receiptType;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String dentalLabLocationId;

	private String dentalLabHospitalId;

	private ModeOfPayment modeOfPayment;

	private Double amountPaid = 0.0;

	private Double remainingAmount = 0.0;

	private Long receivedDate;

	private Boolean discarded = false;

	private User doctor;

	private Location clinic;

	private Location dentalLab;

	private String chequeNumber;

	private String note;

	public Location getClinic() {
		return clinic;
	}

	public void setClinic(Location clinic) {
		this.clinic = clinic;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueReceiptId() {
		return uniqueReceiptId;
	}

	public void setUniqueReceiptId(String uniqueReceiptId) {
		this.uniqueReceiptId = uniqueReceiptId;
	}

	public ReceiptType getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(ReceiptType receiptType) {
		this.receiptType = receiptType;
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

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Double getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(Double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public Long getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Long receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getDentalLab() {
		return dentalLab;
	}

	public void setDentalLab(Location dentalLab) {
		this.dentalLab = dentalLab;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "DentalWorksReceiptResponse [id=" + id + ", uniqueReceiptId=" + uniqueReceiptId + ", receiptType="
				+ receiptType + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", dentalLabLocationId=" + dentalLabLocationId + ", dentalLabHospitalId=" + dentalLabHospitalId
				+ ", modeOfPayment=" + modeOfPayment + ", amountPaid=" + amountPaid + ", remainingAmount="
				+ remainingAmount + ", receivedDate=" + receivedDate + ", discarded=" + discarded + "]";
	}

}
