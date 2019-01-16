package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.OrganizingCommittee;
import com.dpdocter.beans.WorkingHours;

@Document(collection = "doctor_conference_session_cl")
public class DoctorConferenceSessionCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String titleImage;
	@Field
	private String description;
	@Field
	private ObjectId conferenceId;
	@Field
	private Date onDate;
	@Field
	private WorkingHours schedule;
	@Field
	private Address address;
	@Field
	private List<ObjectId> topics;
	@Field
	private List<OrganizingCommittee> speakers;
	@Field
	private Boolean discarded = false;

	public ObjectId getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(ObjectId conferenceId) {
		this.conferenceId = conferenceId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Date getOnDate() {
		return onDate;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}

	public WorkingHours getSchedule() {
		return schedule;
	}

	public void setSchedule(WorkingHours schedule) {
		this.schedule = schedule;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<ObjectId> getTopics() {
		return topics;
	}

	public void setTopics(List<ObjectId> topics) {
		this.topics = topics;
	}

	public List<OrganizingCommittee> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<OrganizingCommittee> speakers) {
		this.speakers = speakers;
	}

}