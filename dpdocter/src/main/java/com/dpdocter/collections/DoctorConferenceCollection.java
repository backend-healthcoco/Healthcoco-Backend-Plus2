package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.OrganizingCommittee;

@Document(collection = "doctor_conference_cl")
public class DoctorConferenceCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String titleImage;
	@Field
	private String description;
	@Field
	private List<ObjectId> specialities;
	@Field
	private Date fromDate;
	@Field
	private Date toDate;
	@Field
	private Address address;
	@Field
	private List<OrganizingCommittee> commiteeMember;
	@Field
	private List<OrganizingCommittee> speakers;
	@Field
	private Boolean discarded = false;
	@Field
	private String status = "NONE";
	@Field
	private String smsHeader;

	public String getSmsHeader() {
		return smsHeader;
	}

	public void setSmsHeader(String smsHeader) {
		this.smsHeader = smsHeader;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ObjectId> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<ObjectId> specialities) {
		this.specialities = specialities;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<OrganizingCommittee> getCommiteeMember() {
		return commiteeMember;
	}

	public void setCommiteeMember(List<OrganizingCommittee> commiteeMember) {
		this.commiteeMember = commiteeMember;
	}

	public List<OrganizingCommittee> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<OrganizingCommittee> speakers) {
		this.speakers = speakers;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
