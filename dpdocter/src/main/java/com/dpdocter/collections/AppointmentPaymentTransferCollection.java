package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "appointment_payment_transfer_cl")
public class AppointmentPaymentTransferCollection extends GenericCollection{

	@Id
	private String id;
	@Field
	private String entity;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId userId;
	
	@Field
	private ObjectId appointmentId;
	
	@Field
	private String source;
	
	@Field
	private String receipient;
	
	@Field
	private Integer amount;
	
	@Field
	private String currency;
	
	@Field
	private Integer amount_reversed;
	
	@Field
	private Integer fees;
	
	@Field
	private Integer tax;
	
	@Field
	private Boolean on_hold;
	
	@Field
	private String on_hold_until;
	
	@Field
	private String recipient_settlement_id;
	
	@Field
	private Long created_at;
	
	@Field
	private Object linked_account_notes;
	
	@Field
	private Object notes;
	
	@Field
	private Long processed_at;



	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getEntity() {
		return entity;
	}


	public void setEntity(String entity) {
		this.entity = entity;
	}


	public ObjectId getDoctorId() {
		return doctorId;
	}


	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}


	public ObjectId getUserId() {
		return userId;
	}


	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public int getAmount_reversed() {
		return amount_reversed;
	}


	public void setAmount_reversed(int amount_reversed) {
		this.amount_reversed = amount_reversed;
	}


	public boolean isOn_hold() {
		return on_hold;
	}


	public void setOn_hold(boolean on_hold) {
		this.on_hold = on_hold;
	}


	

	


	public int getTax() {
		return tax;
	}


	public void setTax(int tax) {
		this.tax = tax;
	}


	public int getFees() {
		return fees;
	}


	public void setFees(int fees) {
		this.fees = fees;
	}


	public ObjectId getAppointmentId() {
		return appointmentId;
	}


	public void setAppointmentId(ObjectId appointmentId) {
		this.appointmentId = appointmentId;
	}


	public String getReceipient() {
		return receipient;
	}


	public void setReceipient(String receipient) {
		this.receipient = receipient;
	}


	public Boolean getOn_hold() {
		return on_hold;
	}


	public void setOn_hold(Boolean on_hold) {
		this.on_hold = on_hold;
	}


	public String getOn_hold_until() {
		return on_hold_until;
	}


	public void setOn_hold_until(String on_hold_until) {
		this.on_hold_until = on_hold_until;
	}


	public String getRecipient_settlement_id() {
		return recipient_settlement_id;
	}


	public void setRecipient_settlement_id(String recipient_settlement_id) {
		this.recipient_settlement_id = recipient_settlement_id;
	}


	public Long getCreated_at() {
		return created_at;
	}


	public void setCreated_at(Long created_at) {
		this.created_at = created_at;
	}


	

	public Object getLinked_account_notes() {
		return linked_account_notes;
	}


	public void setLinked_account_notes(Object linked_account_notes) {
		this.linked_account_notes = linked_account_notes;
	}


	public Object getNotes() {
		return notes;
	}


	public void setNotes(Object notes) {
		this.notes = notes;
	}


	public Long getProcessed_at() {
		return processed_at;
	}


	public void setProcessed_at(Long processed_at) {
		this.processed_at = processed_at;
	}


	public void setAmount(Integer amount) {
		this.amount = amount;
	}


	public void setAmount_reversed(Integer amount_reversed) {
		this.amount_reversed = amount_reversed;
	}


	public void setFees(Integer fees) {
		this.fees = fees;
	}


	public void setTax(Integer tax) {
		this.tax = tax;
	}


	
}
