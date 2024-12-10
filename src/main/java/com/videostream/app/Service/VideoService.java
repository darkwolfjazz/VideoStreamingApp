package com.videostream.app.Service;

import com.videostream.app.entity.VideoEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface VideoService {

//save video

VideoEntity saveVideo(VideoEntity videoEntity, MultipartFile file);

//get video

    VideoEntity getVideo(String videoId);

//get by title
VideoEntity getVideoByTitle(String title);

//get all videos
    List<VideoEntity> getAllVideos();





}
