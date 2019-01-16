package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.OrganizingCommitteeResponse;

public class DoctorConferenceSession extends GenericCollection {

	private String id;

	private String title;

	private String titleImage;

	private String description;

	private String conferenceId;

	private Date onDate;

	private WorkingHours schedules;

	private Address address;

	private List<String> topics;

	private List<OrganizingCommitteeResponse> speakers;

	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	public Date getOnDate() {
		return onDate;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}

	public WorkingHours getSchedules() {
		return schedules;
	}

	public void setSchedules(WorkingHours schedules) {
		this.schedules = schedules;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public List<OrganizingCommitteeResponse> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<OrganizingCommitteeResponse> speakers) {
		this.speakers = speakers;
	}

}
