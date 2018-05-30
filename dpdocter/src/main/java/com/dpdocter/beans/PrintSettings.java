package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.LineStyle;

public class PrintSettings extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String componentType = ComponentType.ALL.getType();

	private PageSetup pageSetup;

	private HeaderSetup headerSetup;

	private FooterSetup footerSetup;

	private PrintSettingsText contentSetup;

	private Boolean discarded = false;

	private String clinicLogoUrl;

	private String hospitalUId;

	private String contentLineSpace = LineSpace.SMALL.name();

	private String contentLineStyle = LineStyle.INLINE.getStyle();

	private boolean showDrugGenericNames = false;

	private String generalNotes;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public PageSetup getPageSetup() {
		return pageSetup;
	}

	public void setPageSetup(PageSetup pageSetup) {
		this.pageSetup = pageSetup;
	}

	public HeaderSetup getHeaderSetup() {
		return headerSetup;
	}

	public void setHeaderSetup(HeaderSetup headerSetup) {
		this.headerSetup = headerSetup;
	}

	public FooterSetup getFooterSetup() {
		return footerSetup;
	}

	public void setFooterSetup(FooterSetup footerSetup) {
		this.footerSetup = footerSetup;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getClinicLogoUrl() {
		return clinicLogoUrl;
	}

	public void setClinicLogoUrl(String clinicLogoUrl) {
		this.clinicLogoUrl = clinicLogoUrl;
	}

	public PrintSettingsText getContentSetup() {
		return contentSetup;
	}

	public void setContentSetup(PrintSettingsText contentSetup) {
		this.contentSetup = contentSetup;
	}

	public String getContentLineSpace() {
		return contentLineSpace;
	}

	public void setContentLineSpace(String contentLineSpace) {
		this.contentLineSpace = contentLineSpace;
	}

	public String getContentLineStyle() {
		return contentLineStyle;
	}

	public void setContentLineStyle(String contentLineStyle) {
		this.contentLineStyle = contentLineStyle;
	}

	public boolean getShowDrugGenericNames() {
		return showDrugGenericNames;
	}

	public void setShowDrugGenericNames(boolean showDrugGenericNames) {
		this.showDrugGenericNames = showDrugGenericNames;
	}

	public String getHospitalUId() {
		return hospitalUId;
	}

	public void setHospitalUId(String hospitalUId) {
		this.hospitalUId = hospitalUId;
	}

	public String getGeneralNotes() {
		return generalNotes;
	}

	public void setGeneralNotes(String generalNotes) {
		this.generalNotes = generalNotes;
	}

	@Override
	public String toString() {
		return "PrintSettings [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", componentType=" + componentType + ", pageSetup=" + pageSetup + ", headerSetup="
				+ headerSetup + ", footerSetup=" + footerSetup + ", contentSetup=" + contentSetup + ", discarded="
				+ discarded + ", clinicLogoUrl=" + clinicLogoUrl + ", hospitalUId=" + hospitalUId
				+ ", contentLineSpace=" + contentLineSpace + ", contentLineStyle=" + contentLineStyle
				+ ", showDrugGenericNames=" + showDrugGenericNames + "]";
	}
}
