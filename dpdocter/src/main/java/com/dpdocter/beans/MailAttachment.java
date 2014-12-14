package com.dpdocter.beans;

import org.springframework.core.io.FileSystemResource;
/**
 * 
 * @author veeraj
 *
 */
public class MailAttachment {
	private String attachmentName;
	private FileSystemResource fileSystemResource;
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	public FileSystemResource getFileSystemResource() {
		return fileSystemResource;
	}
	public void setFileSystemResource(FileSystemResource fileSystemResource) {
		this.fileSystemResource = fileSystemResource;
	}
	@Override
	public String toString() {
		return "MailAttachment [attachmentName=" + attachmentName
				+ ", fileSystemResource=" + fileSystemResource + "]";
	}
	
	
	
}
