package com.dpdocter.response;

import org.springframework.core.io.FileSystemResource;

public class JasperReportResponse {

	private FileSystemResource fileSystemResource;

	private String path;

	public FileSystemResource getFileSystemResource() {
		return fileSystemResource;
	}

	public void setFileSystemResource(FileSystemResource fileSystemResource) {
		this.fileSystemResource = fileSystemResource;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "JasperReportResponse [fileSystemResource=" + fileSystemResource + ", path=" + path + "]";
	}
}
