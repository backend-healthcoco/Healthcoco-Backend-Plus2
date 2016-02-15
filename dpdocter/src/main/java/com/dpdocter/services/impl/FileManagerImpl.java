package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.services.FileManager;

@Service
public class FileManagerImpl implements FileManager {

    @Value(value = "${IMAGE_RESOURCE}")
    private String imageResource;

     @Value(value = "${bucketName}")
     private String bucketName;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Value(value = "${mail.aws.key.id}")
    private String AWS_KEY;

    @Value(value = "${mail.aws.secret.key}")
    private String AWS_SECRET_KEY;
  
    private void createDirIfNotExist(String dirPath) {
	File dir = new File(dirPath);
	if (!dir.exists()) {
	    dir.mkdirs();
	}
    }

    @Override
    public String saveImageAndReturnImageUrl(FileDetails fileDetails, String path) throws Exception {
	String fileName = fileDetails.getFileName() + "." + fileDetails.getFileExtension();
	String imageUrl = path + "/" + fileName;
		
    	BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
        AmazonS3 s3client = new AmazonS3Client(credentials);
        try {
        	byte[] base64 = Base64.decodeBase64(fileDetails.getFileEncoded());
        	InputStream fis = new ByteArrayInputStream(base64); 
        	String contentType = URLConnection.guessContentTypeFromStream(fis);
        	ObjectMetadata metadata = new ObjectMetadata();
        	metadata.setContentEncoding(fileDetails.getFileExtension());
        	metadata.setContentType(contentType);
            s3client.putObject(new PutObjectRequest(bucketName, imageUrl, fis, metadata));

         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return imageUrl;
        
}

    @Override
    public String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path) {
	String thumbnailUrl = "";
	BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);
	try {
	    img.createGraphics()
		    .drawImage(ImageIO
			    .read(new File(
				    imageResource + File.separator + path + File.separator + fileDetails.getFileName() + "." + fileDetails.getFileExtension()))
			    .getScaledInstance(120, 120, Image.SCALE_SMOOTH), 0, 0, null);

	    String fileName = fileDetails.getFileName() + "_thumb." + fileDetails.getFileExtension();
	    createDirIfNotExist(imageResource + File.separator + path);
	    String filePath = imageResource + File.separator + path + File.separator + fileName;
	    ImageIO.write(img, fileDetails.getFileExtension(), new File(filePath));

	    thumbnailUrl = path + "/" + fileName;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return thumbnailUrl;
    }
}
