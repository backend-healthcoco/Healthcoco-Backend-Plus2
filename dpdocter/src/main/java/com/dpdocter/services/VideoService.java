package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.MyVideo;
import com.dpdocter.beans.Video;
import com.dpdocter.request.AddMyVideoRequest;
import com.dpdocter.request.AddVideoRequest;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface VideoService {

	List<Video> getVideos(String doctorId, String searchTerm, int page, int size);

	Video addVideo(FormDataBodyPart file, AddVideoRequest request);

	MyVideo addMyVideo(FormDataBodyPart file, AddMyVideoRequest request);

	List<MyVideo> getMyVideos(String doctorId, String searchTerm, int page, int size);

	List<Video> getLocationVideos(String doctorId, String locationId, String hospitalId, String searchTerm, int page,
			int size, List<String> tags);

}
