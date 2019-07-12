package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Video;
import com.dpdocter.request.AddVideoRequest;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface VideoService {

	List<Video> getVideos(String doctorId, String searchTerm, long page, int size);

	Video addVideo(FormDataBodyPart file, AddVideoRequest request);

}
