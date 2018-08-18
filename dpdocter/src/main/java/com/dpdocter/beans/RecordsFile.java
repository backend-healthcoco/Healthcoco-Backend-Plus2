package com.dpdocter.beans;

import java.util.List;

public class RecordsFile {

	private String fileId;

	private String recordsUrl;

	private String thumbnailUrl;

	private String recordsPath;

	private String recordsFileLabel;

	private String recordsType;

	private List<String> pdfInImgs;

	private Double fileSizeInMB = 0.0;

	public List<String> getPdfInImgs() {
		return pdfInImgs;
	}

	public void setPdfInImgs(List<String> pdfInImgs) {
		this.pdfInImgs = pdfInImgs;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getRecordsUrl() {
		return recordsUrl;
	}

	public void setRecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	public String getRecordsPath() {
		return recordsPath;
	}

	public void setRecordsPath(String recordsPath) {
		this.recordsPath = recordsPath;
	}

	public String getRecordsType() {
		return recordsType;
	}

	public void setRecordsType(String recordsType) {
		this.recordsType = recordsType;
	}

	public String getRecordsFileLabel() {
		return recordsFileLabel;
	}

	public void setRecordsFileLabel(String recordsFileLabel) {
		this.recordsFileLabel = recordsFileLabel;
	}

	public Double getFileSizeInMB() {
		return fileSizeInMB;
	}

	public void setFileSizeInMB(Double fileSizeInMB) {
		this.fileSizeInMB = fileSizeInMB;
	}

}
