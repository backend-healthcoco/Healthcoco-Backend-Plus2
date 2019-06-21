package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;

@Document(collection = "doctor_conference_agenda_cl")
public class DoctorConferenceAgendaCollection {
	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private Date onDate;
	@Field
	private WorkingHours schedule;
	@Field
	private String titleImage;
	@Field
	private String colorCode;
	@Field
	private ObjectId conferenceId;
	@Field
	private Boolean discarded = false;

	public Date getOnDate() {
		return onDate;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
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

	public WorkingHours getSchedule() {
		return schedule;
	}

	public void setSchedule(WorkingHours schedule) {
		this.schedule = schedule;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

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

}
