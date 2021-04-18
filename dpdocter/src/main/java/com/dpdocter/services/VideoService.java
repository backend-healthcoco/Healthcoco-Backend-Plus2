package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.MyVideo;
import com.dpdocter.beans.Video;
import com.dpdocter.request.AddMyVideoRequest;
import com.dpdocter.request.AddVideoRequest;

public interface VideoService {

	Video addVideo(MultipartFile file, AddVideoRequest request);

	MyVideo addMyVideo(MultipartFile file, AddMyVideoRequest request);

	List<MyVideo> getMyVideos(String doctorId, String searchTerm, int page, int size);

	List<Video> getLocationVideos(String doctorId, String locationId, String hospitalId, String searchTerm, int page,
			int size, List<String> tags);

	List<Video> getVideos(String doctorId, String searchTerm, List<String> tags, int page, int size);

}
