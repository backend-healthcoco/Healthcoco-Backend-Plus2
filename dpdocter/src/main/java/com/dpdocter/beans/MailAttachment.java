package com.dpdocter.beans;

import java.io.InputStream;

import org.springframework.core.io.FileSystemResource;

/**
 * 
 * @author veeraj
 *
 */
public class MailAttachment {
    private String attachmentName;

    private FileSystemResource fileSystemResource;

    private InputStream inputStream;

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

    public InputStream getInputStream() {
	return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
	this.inputStream = inputStream;
    }

    @Override
    public String toString() {
	return "MailAttachment [attachmentName=" + attachmentName + ", fileSystemResource=" + fileSystemResource + ", inputStream=" + inputStream + "]";
    }
}
