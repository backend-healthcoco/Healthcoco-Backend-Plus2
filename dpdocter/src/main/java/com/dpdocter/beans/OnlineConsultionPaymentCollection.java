package com.dpdocter.beans;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;

	@Document(collection = "online_consultation_payment_cl")
	public class OnlineConsultionPaymentCollection extends GenericCollection {

		@Id
		private ObjectId id;

		@Field
		private ObjectId userId;

		@Field
		private ObjectId problemDetailsId;

		@Field
		private String transactionId;
		
		@Field
		private ObjectId doctorId;	
		
		@Field
		private String razorPayAccountId;


		@Field
		private String transactionStatus;
		@Field
		private Discount discount;
		@Field
		private Double amount = 0.0;

		@Field
		private Double discountAmount = 0.0;

		@Field
		private Double transferAmount = 0.0;
		@Field
		private PaymentMode mode = PaymentMode.ONLINE;
		@Field
		private String chequeNo;
		@Field
		private String orderId;
		@Field
		private String reciept;
		@Field
		private String accountNo;
		@Field
		private String bankName;
		@Field
		private String branch;
		@Field
		private Date chequeDate;
		@Field
		private UserResponse paymentBy;
		@Field
		private DoctorConsultation consultationType;

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public ObjectId getUserId() {
			return userId;
		}

		public void setUserId(ObjectId userId) {
			this.userId = userId;
		}

		public ObjectId getProblemDetailsId() {
			return problemDetailsId;
		}

		public void setProblemDetailsId(ObjectId problemDetailsId) {
			this.problemDetailsId = problemDetailsId;
		}

		

		public String getTransactionId() {
			return transactionId;
		}

		public void setTransactionId(String transactionId) {
			this.transactionId = transactionId;
		}

		public String getTransactionStatus() {
			return transactionStatus;
		}

		public void setTransactionStatus(String transactionStatus) {
			this.transactionStatus = transactionStatus;
		}

		public Discount getDiscount() {
			return discount;
		}

		public void setDiscount(Discount discount) {
			this.discount = discount;
		}

		public Double getAmount() {
			return amount;
		}

		public void setAmount(Double amount) {
			this.amount = amount;
		}

		public Double getDiscountAmount() {
			return discountAmount;
		}

		public void setDiscountAmount(Double discountAmount) {
			this.discountAmount = discountAmount;
		}

		public Double getTransferAmount() {
			return transferAmount;
		}

		public void setTransferAmount(Double transferAmount) {
			this.transferAmount = transferAmount;
		}

		public PaymentMode getMode() {
			return mode;
		}

		public void setMode(PaymentMode mode) {
			this.mode = mode;
		}

		public String getChequeNo() {
			return chequeNo;
		}

		public void setChequeNo(String chequeNo) {
			this.chequeNo = chequeNo;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getReciept() {
			return reciept;
		}

		public void setReciept(String reciept) {
			this.reciept = reciept;
		}

		public String getAccountNo() {
			return accountNo;
		}

		public void setAccountNo(String accountNo) {
			this.accountNo = accountNo;
		}

		public String getBankName() {
			return bankName;
		}

		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public Date getChequeDate() {
			return chequeDate;
		}

		public void setChequeDate(Date chequeDate) {
			this.chequeDate = chequeDate;
		}

		public UserResponse getPaymentBy() {
			return paymentBy;
		}

		public void setPaymentBy(UserResponse paymentBy) {
			this.paymentBy = paymentBy;
		}

		public ObjectId getDoctorId() {
			return doctorId;
		}

		public void setDoctorId(ObjectId doctorId) {
			this.doctorId = doctorId;
		}

		public String getRazorPayAccountId() {
			return razorPayAccountId;
		}

		public void setRazorPayAccountId(String razorPayAccountId) {
			this.razorPayAccountId = razorPayAccountId;
		}

		public DoctorConsultation getConsultationType() {
			return consultationType;
		}

		public void setConsultationType(DoctorConsultation consultationType) {
			this.consultationType = consultationType;
		}
		
		


}
