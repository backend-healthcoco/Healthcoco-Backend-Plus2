package com.dpdocter.response;

import java.io.InputStream;

public class JasperReportResponse {

	private InputStream inputStream;
	
	private String path;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "JasperReportResponse [inputStream=" + inputStream + ", path=" + path + "]";
	}
}
