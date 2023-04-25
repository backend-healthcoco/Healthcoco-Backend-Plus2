package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

public class DoctorPatientReceiptLookupResponse extends GenericCollection {

	private String id;

	private String uniqueReceiptId;

	private ReceiptType receiptType;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private ModeOfPayment modeOfPayment;

	private List<AdvanceReceiptIdWithAmountResponse> advanceReceiptIdWithAmounts;

	private String invoiceId;

	private String uniqueInvoiceId;

	private Double amountPaid = 0.0;

	private Double remainingAdvanceAmount = 0.0;

	private Double balanceAmount = 0.0;

	private Date receivedDate;

	private Boolean discarded = false;

	private Double usedAdvanceAmount = 0.0;

	private DoctorPatientInvoiceCollection invoiceCollection;

	private PatientCollection patient;

	private UserCollection patientUser;

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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public List<AdvanceReceiptIdWithAmountResponse> getAdvanceReceiptIdWithAmounts() {
		return advanceReceiptIdWithAmounts;
	}

	public void setAdvanceReceiptIdWithAmounts(List<AdvanceReceiptIdWithAmountResponse> advanceReceiptIdWithAmounts) {
		this.advanceReceiptIdWithAmounts = advanceReceiptIdWithAmounts;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Double getRemainingAdvanceAmount() {
		return remainingAdvanceAmount;
	}

	public void setRemainingAdvanceAmount(Double remainingAdvanceAmount) {
		this.remainingAdvanceAmount = remainingAdvanceAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public PatientCollection getPatient() {
		return patient;
	}

	public void setPatient(PatientCollection patient) {
		this.patient = patient;
	}

	public UserCollection getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(UserCollection patientUser) {
		this.patientUser = patientUser;
	}

	public DoctorPatientInvoiceCollection getInvoiceCollection() {
		return invoiceCollection;
	}

	public void setInvoiceCollection(DoctorPatientInvoiceCollection invoiceCollection) {
		this.invoiceCollection = invoiceCollection;
	}

	@Override
	public String toString() {
		return "DoctorPatientReceiptLookupResponse [id=" + id + ", uniqueReceiptId=" + uniqueReceiptId
				+ ", receiptType=" + receiptType + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", modeOfPayment=" + modeOfPayment
				+ ", advanceReceiptIdWithAmounts=" + advanceReceiptIdWithAmounts + ", invoiceId=" + invoiceId
				+ ", uniqueInvoiceId=" + uniqueInvoiceId + ", amountPaid=" + amountPaid + ", remainingAdvanceAmount="
				+ remainingAdvanceAmount + ", balanceAmount=" + balanceAmount + ", receivedDate=" + receivedDate
				+ ", discarded=" + discarded + ", usedAdvanceAmount=" + usedAdvanceAmount + ", invoiceCollection="
				+ invoiceCollection + ", patient=" + patient + ", patientUser=" + patientUser + "]";
	}
}