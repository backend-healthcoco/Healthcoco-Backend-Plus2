package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value = "payment_transfer_cl")
public class PaymentTransferCollection extends GenericCollection {

	@Id
	private String id;
	@Field
	private String entity;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId userId;
	@Field
	private String source;
	@Field
	private String recipient;
	@Field
	private int amount;
	@Field
	private String currency = "INR";
	@Field
	private int amount_reversed = 0;
	@Field
	private boolean on_hold = false;
	@Field
	private Object notes;
	@Field
	private int tax;
	@Field
	private int fees;
	@Field
	private Date created_at;
	@Field
	private Object linked_account_notes;
	@Field
	private int on_hold_until = 0;;
	@Field
	private String recipient_settlement_id;
	@Field
	private long processed_at;
	@Field
	private String orderId;
	@Field
	private ObjectId problemDetailsId;

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

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
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

	public Object getNotes() {
		return notes;
	}

	public void setNotes(Object notes) {
		this.notes = notes;
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

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Object getLinked_account_notes() {
		return linked_account_notes;
	}

	public void setLinked_account_notes(Object linked_account_notes) {
		this.linked_account_notes = linked_account_notes;
	}

	public int getOn_hold_until() {
		return on_hold_until;
	}

	public void setOn_hold_until(int on_hold_until) {
		this.on_hold_until = on_hold_until;
	}

	public String getRecipient_settlement_id() {
		return recipient_settlement_id;
	}

	public void setRecipient_settlement_id(String recipient_settlement_id) {
		this.recipient_settlement_id = recipient_settlement_id;
	}

	public long getProcessed_at() {
		return processed_at;
	}

	public void setProcessed_at(long processed_at) {
		this.processed_at = processed_at;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public ObjectId getProblemDetailsId() {
		return problemDetailsId;
	}

	public void setProblemDetailsId(ObjectId problemDetailsId) {
		this.problemDetailsId = problemDetailsId;
	}

}
