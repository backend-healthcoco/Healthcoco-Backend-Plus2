package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "abused_report_cl")
public class AbusedReportCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String abusedBy;

	@Field
	private String abusedTo;

	@Field
	private String reason;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getAbusedBy() {
		return abusedBy;
	}

	public void setAbusedBy(String abusedBy) {
		this.abusedBy = abusedBy;
	}

	public String getAbusedTo() {
		return abusedTo;
	}

	public void setAbusedTo(String abusedTo) {
		this.abusedTo = abusedTo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "AbusedReportCollection [id=" + id + ", abusedBy=" + abusedBy + ", abusedTo=" + abusedTo + ", reason="
				+ reason + "]";
	}

}
