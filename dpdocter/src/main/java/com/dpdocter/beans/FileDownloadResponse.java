package com.dpdocter.beans;

import java.io.InputStream;

public class FileDownloadResponse {

	private String fileName;

    private InputStream inputStream;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public String toString() {
		return "FileDownloadResponse [fileName=" + fileName + ", inputStream=" + inputStream + "]";
	}
}
