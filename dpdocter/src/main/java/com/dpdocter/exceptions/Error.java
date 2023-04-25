package com.dpdocter.exceptions;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Error implements Serializable {

	private static final long serialVersionUID = 1L;

	public Error() {
	}

	public Error(String source, String reasonCode) {
		this(source, reasonCode, null);
	}

	public Error(String source, String reasonCode, String description) {
		this(source, reasonCode, description, false);
	}

	public Error(String source, String reasonCode, String description, Boolean recoverable) {
		this.source = source;
		this.reasonCode = reasonCode;
		this.description = description;
		this.recoverable = recoverable;
	}

	@XmlElement(name = "Source", required = true, nillable = false)
	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	private String source = null;

	@XmlElement(name = "ReasonCode", required = true, nillable = false)
	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(final String reasonCode) {
		this.reasonCode = reasonCode;
	}

	private String reasonCode = null;

	@XmlElement(name = "Description", required = false, nillable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	private String description = null;

	@XmlElement(name = "Recoverable", required = true, nillable = false)
	public Boolean getRecoverable() {
		return recoverable;
	}

	public void setRecoverable(final Boolean recoverable) {
		this.recoverable = recoverable;
	}

	private Boolean recoverable = null;

	@Override
	public String toString() {
		return "Error [source=" + source + ", reasonCode=" + reasonCode + ", description=" + description
				+ ", recoverable=" + recoverable + "]";
	}

}