package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
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
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

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
	public ImageURLResponse saveImageAndReturnImageUrl(FileDetails fileDetails, String path, Boolean createThumbnail)
			throws Exception {
		ImageURLResponse response = new ImageURLResponse();
		String fileName = fileDetails.getFileName() + "." + fileDetails.getFileExtension();
		String imageUrl = path + "/" + fileName;

		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		try {
			byte[] base64 = Base64.decodeBase64(fileDetails.getFileEncoded());
			InputStream fis = new ByteArrayInputStream(base64);
			String contentType = URLConnection.guessContentTypeFromStream(fis);
			if (!DPDoctorUtils.anyStringEmpty(contentType) && contentType.equalsIgnoreCase("exe")) {
				throw new BusinessException(ServiceError.NotAcceptable, "Invalid File");
			}
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(new ByteArrayInputStream(base64));
			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(fileDetails.getFileExtension());
			metadata.setContentType(contentType);
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, imageUrl, fis, metadata));
			response.setImageUrl(imageUrl);
			if (createThumbnail) {
				response.setThumbnailUrl(saveThumbnailAndReturnThumbNailUrl(fileDetails, path));
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
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

			S3Object object = s3client.getObject(new GetObjectRequest(bucketName,
					path + File.separator + fileDetails.getFileName() + "." + fileDetails.getFileExtension()));
			InputStream objectData = object.getObjectContent();

			BufferedImage originalImage = ImageIO.read(objectData);
			double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
			int height = originalImage.getHeight();

			int width = originalImage.getWidth();
			int max = 120;
			if (width == height) {
				width = max;
				height = max;
			} else if (width > height) {
				height = max;
				width = (int) (ratio * max);
			} else {
				width = max;
				height = (int) (max / ratio);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0,
					null);
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
			System.out.println("Error Message: " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
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
	public Double saveRecord(FormDataBodyPart file, String recordPath, Double allowedSize, Boolean checkSize) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		Double fileSizeInMB = 0.0;
		try {
			InputStream fis = file.getEntityAs(InputStream.class);
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);
			if (checkSize) {
				fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000 * 1000)).doubleValue();
				if (fileSizeInMB > allowedSize) {
					throw new BusinessException(ServiceError.Unknown,
							allowedSize + " MB are left. You cannot upload file more than this");
				}
			}
			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentDisposition().getType());
			metadata.setContentType(file.getMediaType().getType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(
					new PutObjectRequest(bucketName, recordPath, file.getEntityAs(InputStream.class), metadata));

		} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}
		return fileSizeInMB;
	}

	@Override
	@Transactional
	public Double saveRecordBase64(FileDetails fileDetail, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		Double fileSizeInMB = 0.0;
		try {
			byte[] base64 = Base64.decodeBase64(fileDetail.getFileEncoded());
			InputStream fis = new ByteArrayInputStream(base64);
			String contentType = URLConnection.guessContentTypeFromStream(fis);
			if (!DPDoctorUtils.anyStringEmpty(contentType) && contentType.equalsIgnoreCase("exe")) {
				throw new BusinessException(ServiceError.NotAcceptable, "Invalid File");
			}
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(new ByteArrayInputStream(base64));

			fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000 * 1000)).doubleValue();

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(fileDetail.getFileExtension());
			metadata.setContentType(contentType);
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, recordPath, fis, metadata));

		} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}
		return fileSizeInMB;
	}

	@Override
	@Transactional
	public ImageURLResponse saveImage(FormDataBodyPart file, String recordPath, Boolean createThumbnail) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		ImageURLResponse response = new ImageURLResponse();
		try {
			InputStream fis = file.getEntityAs(InputStream.class);
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);

			/*
			 * fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000
			 * * 1000)).doubleValue(); if (fileSizeInMB > 10) { throw new
			 * BusinessException(ServiceError.Unknown,
			 * " You cannot upload file more than 1O mb"); }
			 */

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentDisposition().getType());
			metadata.setContentType(file.getMediaType().getType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			s3client.putObject(
					new PutObjectRequest(bucketName, recordPath, file.getEntityAs(InputStream.class), metadata));
			response.setImageUrl(imagePath + recordPath);
			if (createThumbnail) {
				response.setThumbnailUrl(imagePath + saveThumbnailUrl(file, recordPath));
			}
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());

		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;

	}

	@Override
	@Transactional
	public String saveThumbnailUrl(FormDataBodyPart file, String path) {
		String thumbnailUrl = "";

		try {
			FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
			String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
			BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
			AmazonS3 s3client = new AmazonS3Client(credentials);
			S3Object object = s3client.getObject(new GetObjectRequest(bucketName, path));
			InputStream objectData = object.getObjectContent();

			BufferedImage originalImage = ImageIO.read(objectData);
			double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
			int height = originalImage.getHeight();

			int width = originalImage.getWidth();
			int max = 120;
			if (width == height) {
				width = max;
				height = max;
			} else if (width > height) {
				height = max;
				width = (int) (ratio * max);
			} else {
				width = max;
				height = (int) (max / ratio);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0,
					null);
			// String fileName = fileDetails.getFileName() + "_thumb." +
			// fileDetails.getFileExtension();
			thumbnailUrl = "thumb_" + path;

			originalImage.flush();
			originalImage = null;

			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			ImageIO.write(img, fileExtension, outstream);
			byte[] buffer = outstream.toByteArray();
			objectData = new ByteArrayInputStream(buffer);

			String contentType = URLConnection.guessContentTypeFromStream(objectData);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(buffer.length);
			metadata.setContentEncoding(fileExtension);
			metadata.setContentType(contentType);
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			s3client.putObject(new PutObjectRequest(bucketName, thumbnailUrl, objectData, metadata));
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message: " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
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
	public List<String> convertPdfToImage(FileDetails fileDetails, String path, Boolean createThumbnail)
			throws Exception {
		FileDetails fileDetail = null;
		byte[] base64 = Base64.decodeBase64(fileDetails.getFileEncoded());
		PDDocument document = PDDocument.load(base64);
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		document.getPages().getCount();
		List<String> imagelist = new ArrayList<String>();
		ByteArrayOutputStream outstream = null;
		ImageURLResponse imageURLResponse = null;
		for (int i = 0; i < document.getPages().getCount(); i++) {
			imageURLResponse = new ImageURLResponse();
			fileDetail = new FileDetails();
			// note that the page number parameter is zero based
			BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 500, ImageType.RGB);
			outstream = new ByteArrayOutputStream();
			ImageIOUtil.writeImage(bim, "jpg", outstream);
			// suffix in filename will be used as the file format

			// imageURLResponse = saveImageAndReturnImageUrl(outstream,
			// fileDetails.getFileName().replace(" ", "") + "-" + (i) , path, false);
			fileDetail.setFileEncoded(Base64.encodeBase64String(outstream.toByteArray()));

			fileDetail.setFileName(fileDetails.getFileName().replace(" ", "") + "-" + (i));
			fileDetail.setFileExtension("jpg");
			imageURLResponse = saveImageAndReturnImageUrl(fileDetail, path, false);
			if (imageURLResponse != null && !DPDoctorUtils.anyStringEmpty(imageURLResponse.getImageUrl()))
				imagelist.add(imagePath + imageURLResponse.getImageUrl());
		}
		System.out.println(imagelist.size());
		document.close();

		return imagelist;
	}

}
