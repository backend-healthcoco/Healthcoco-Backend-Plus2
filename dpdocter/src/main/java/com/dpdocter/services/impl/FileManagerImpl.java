package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.sun.jersey.multipart.FormDataBodyPart;

@Service
public class FileManagerImpl implements FileManager {

    @Value(value = "${image.path}")
    private String imagePath;

    @Value(value = "${bucket.name}")
    private String bucketName;

    @Value(value = "${mail.aws.key.id}")
    private String AWS_KEY;

    @Value(value = "${mail.aws.secret.key}")
    private String AWS_SECRET_KEY;
  
    @Override
    @Transactional
    public ImageURLResponse saveImageAndReturnImageUrl(FileDetails fileDetails, String path, Boolean createThumbnail) throws Exception {
    ImageURLResponse response = new ImageURLResponse();
	String fileName = fileDetails.getFileName() + "." + fileDetails.getFileExtension();
	String imageUrl = path + "/" + fileName;

	BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	AmazonS3 s3client = new AmazonS3Client(credentials);
	try {
		byte[] base64 = Base64.decodeBase64(fileDetails.getFileEncoded());
	    InputStream fis = new ByteArrayInputStream(base64);
	    String contentType = URLConnection.guessContentTypeFromStream(fis);
	    
	    ObjectMetadata metadata = new ObjectMetadata();
	    byte[] contentBytes = IOUtils.toByteArray(new ByteArrayInputStream(base64));
	    metadata.setContentLength(contentBytes.length);
	    metadata.setContentEncoding(fileDetails.getFileExtension());
	    metadata.setContentType(contentType);
	    metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);     
	    
	    s3client.putObject(new PutObjectRequest(bucketName, imageUrl, fis, metadata));
	    response.setImageUrl(imageUrl);
	    if(createThumbnail){
	    	BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(base64));
		    double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
		    int height = originalImage.getHeight();	

		    int width = originalImage.getWidth();
		    int max = 120;
		    if (width == height) {
		    	width = max;
		    	height = max;
		    } 
		    else if (width > height) {
		    	height = max;
		    	width = (int) (ratio * max);
		    }
		    else {
		    	width = max;
		    	height = (int) (max / ratio);
		    }
		    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		    img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		    fileName = fileDetails.getFileName() + "_thumb." + fileDetails.getFileExtension();
		    String thumbnailUrl = path + "/" + fileName;

		    originalImage.flush();
		    originalImage = null;
		    
		    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		    ImageIO.write(img, fileDetails.getFileExtension(), outstream);
		    byte[] buffer = outstream.toByteArray();
		    InputStream objectData = new ByteArrayInputStream(buffer);

		    contentType = URLConnection.guessContentTypeFromStream(objectData);
		    ObjectMetadata objectMetadata = new ObjectMetadata();
		    metadata.setContentLength(buffer.length);
		    metadata.setContentEncoding(fileDetails.getFileExtension());
		    metadata.setContentType(contentType);
		    metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
		    
		    AmazonS3 s3clientOther = new AmazonS3Client(credentials);
		    s3clientOther.putObject(new PutObjectRequest(bucketName, thumbnailUrl, objectData, objectMetadata));
	    	response.setThumbnailUrl(saveThumbnailAndReturnThumbNailUrl(fileDetails, path));
	    }
	} catch (AmazonServiceException ase) {
	    System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode() + " AWS Error Code:   "
		    + ase.getErrorCode() + " Error Type:       " + ase.getErrorType() + " Request ID:       " + ase.getRequestId());
	} catch (AmazonClientException ace) {
	    System.out.println(
		    "Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
	    System.out.println("Error Message: " + ace.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path) {
	String thumbnailUrl = "";
	
	try {
	    BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	    AmazonS3 s3client = new AmazonS3Client(credentials);

	    S3Object object = s3client
		    .getObject(new GetObjectRequest(bucketName, path + File.separator + fileDetails.getFileName() + "." + fileDetails.getFileExtension()));
	    InputStream objectData = object.getObjectContent();

	    BufferedImage originalImage = ImageIO.read(objectData);
	    double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
	    int height = originalImage.getHeight();	

	    int width = originalImage.getWidth();
	    int max = 120;
	    if (width == height) {
	    	width = max;
	    	height = max;
	    } 
	    else if (width > height) {
	    	height = max;
	    	width = (int) (ratio * max);
	    }
	    else {
	    	width = max;
	    	height = (int) (max / ratio);
	    }
	    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
	    String fileName = fileDetails.getFileName() + "_thumb." + fileDetails.getFileExtension();
	    thumbnailUrl = path + "/" + fileName;

	    originalImage.flush();
	    originalImage = null;
	    
	    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
	    ImageIO.write(img, fileDetails.getFileExtension(), outstream);
	    byte[] buffer = outstream.toByteArray();
	    objectData = new ByteArrayInputStream(buffer);

	    String contentType = URLConnection.guessContentTypeFromStream(objectData);
	    ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentLength(buffer.length);
	    metadata.setContentEncoding(fileDetails.getFileExtension());
	    metadata.setContentType(contentType);
	    metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
	    s3client.putObject(new PutObjectRequest(bucketName, thumbnailUrl, objectData, metadata));
	} catch (AmazonServiceException ase) {
	    System.out.println("Error Message: " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode() + " AWS Error Code:   " + ase.getErrorCode()
		    + " Error Type:       " + ase.getErrorType() + " Request ID:       " + ase.getRequestId());
	} catch (AmazonClientException ace) {
	    System.out.println(
		    "Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
	    System.out.println("Error Message: " + ace.getMessage());
	} catch (Exception e) {
	    System.out.println("Error Message: " + e.getMessage());
	}
	return thumbnailUrl;
    }

    @Override
    @Transactional
    public void saveRecord(FormDataBodyPart file, String recordPath) {
	BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	AmazonS3 s3client = new AmazonS3Client(credentials);
	try {
		InputStream fis = file.getEntityAs(InputStream.class);
	    ObjectMetadata metadata = new ObjectMetadata();
	    byte[] contentBytes = IOUtils.toByteArray(fis);
	    metadata.setContentLength(contentBytes.length);
	    metadata.setContentEncoding(file.getContentDisposition().getType());
	    metadata.setContentType(file.getMediaType().getType());
	    metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
	    s3client.putObject(new PutObjectRequest(bucketName, recordPath, fis, metadata));

	} catch (AmazonServiceException ase) {
	    System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode() + " AWS Error Code:   "
		    + ase.getErrorCode() + " Error Type:       " + ase.getErrorType() + " Request ID:       " + ase.getRequestId());
	} catch (AmazonClientException ace) {
	    System.out.println(
		    "Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
	    System.out.println("Error Message: " + ace.getMessage());
	} catch (Exception e) {
	    System.out.println("Error Message: " + e.getMessage());
	}
    }
}
