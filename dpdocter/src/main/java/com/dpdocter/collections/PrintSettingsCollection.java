package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FooterSetup;
import com.dpdocter.beans.HeaderSetup;
import com.dpdocter.beans.PageSetup;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.LineStyle;
import com.dpdocter.enums.PrintSettingType;

@Document(collection = "print_settings_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class PrintSettingsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private String componentType = ComponentType.ALL.getType();

	@Field
    private String printSettingType = PrintSettingType.DEFAULT.getType();
	
	@Field
	private PageSetup pageSetup;

	@Field
	private HeaderSetup headerSetup;

	@Field
	private FooterSetup footerSetup;

	@Field
	private Boolean discarded = false;

	@Field
	private String clinicLogoUrl;

	@Field
	private String hospitalUId;

	@Field
	private PrintSettingsText contentSetup;

	@Field
	private String contentLineSpace = LineSpace.SMALL.name();

	@Field
	private String contentLineStyle = LineStyle.INLINE.getStyle();

	@Field
	private boolean showDrugGenericNames = false;

	@Field
	private Boolean showPoweredBy = true;

	@Field
	private String generalNotes;

	@Field
	private Boolean isLab = false;

	@Field
	private Boolean isPidHasDate = true;
	
	@Field
	private boolean isPaymentShow = false;

	public Boolean getShowPoweredBy() {
		return showPoweredBy;
	}

	public void setShowPoweredBy(Boolean showPoweredBy) {
		this.showPoweredBy = showPoweredBy;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
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

	public Boolean getIsPidHasDate() {
		return isPidHasDate;
	}

	public void setIsPidHasDate(Boolean isPidHasDate) {
		this.isPidHasDate = isPidHasDate;
	}
	
	public Boolean getIsLab() {
		return isLab;
	}

	public void setIsLab(Boolean isLab) {
		this.isLab = isLab;
	}

	public String getPrintSettingType() {
		return printSettingType;
	}

	public void setPrintSettingType(String printSettingType) {
		this.printSettingType = printSettingType;
	}

	public boolean getIsPaymentShow() {
		return isPaymentShow;
	}

	public void setIsPaymentShow(boolean isPaymentShow) {
		this.isPaymentShow = isPaymentShow;
	}

	@Override
	public String toString() {
		return "PrintSettingsCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", componentType=" + componentType + ", pageSetup=" + pageSetup
				+ ", headerSetup=" + headerSetup + ", footerSetup=" + footerSetup + ", discarded=" + discarded
				+ ", clinicLogoUrl=" + clinicLogoUrl + ", hospitalUId=" + hospitalUId + ", contentSetup=" + contentSetup
				+ ", contentLineSpace=" + contentLineSpace + ", contentLineStyle=" + contentLineStyle
				+ ", showDrugGenericNames=" + showDrugGenericNames + ", showPoweredBy=" + showPoweredBy
				+ ", generalNotes=" + generalNotes + ", isLab=" + isLab + ", isPidHasDate=" + isPidHasDate + "]";
	}

}
