package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Code;

@Document(collection = "generic_codes_and_reactions_cl")
public class GenericCodesAndReactionsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private List<Code> codes;

	@Field
	private String genericCode;

	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<Code> getCodes() {
		return codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public String getGenericCode() {
		return genericCode;
	}

	public void setGenericCode(String genericCode) {
		this.genericCode = genericCode;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "GenericCodesAndReactionsCollection [id=" + id + ", codes=" + codes + ", genericCode=" + genericCode
				+ ", discarded=" + discarded + "]";
	}
}
