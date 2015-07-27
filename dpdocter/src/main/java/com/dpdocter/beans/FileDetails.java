package com.dpdocter.beans;

public class FileDetails {

    private String fileEncoded;

    private String fileDecoded;

    private String fileName;

    private String fileExtension;

    public String getFileEncoded() {
	return fileEncoded;
    }

    public void setFileEncoded(String fileEncoded) {
	this.fileEncoded = fileEncoded;
    }

    public String getFileDecoded() {
	return fileDecoded;
    }

    public void setFileDecoded(String fileDecoded) {
	this.fileDecoded = fileDecoded;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public String getFileExtension() {
	return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
	this.fileExtension = fileExtension;
    }

    @Override
    public String toString() {
	return "FileDetails [fileEncoded=" + fileEncoded + ", fileDecoded=" + fileDecoded + ", fileName=" + fileName + ", fileExtension=" + fileExtension + "]";
    }

}
