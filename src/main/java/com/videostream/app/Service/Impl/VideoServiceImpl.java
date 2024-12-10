package com.videostream.app.Service.Impl;

import com.videostream.app.Service.VideoService;
import com.videostream.app.entity.VideoEntity;
import com.videostream.app.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    private Logger logger=org.slf4j.LoggerFactory.getLogger(VideoServiceImpl.class);


    @Autowired
    private VideoRepository videoRepository;

    @Value("${files.video}") String DIR;
    @PostConstruct
    public void init(){
        File file=new File(DIR);
        if(!file.exists()){
            file.mkdir();
            logger.info("Folder created:{}",DIR);
        }else{
            logger.info("Folder already exists:{}",DIR);
        }
    }

    @Override
    public VideoEntity saveVideo(VideoEntity videoEntity, MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();//returns original file name
            String contentType = file.getContentType();//returns file type
            InputStream inputStream = file.getInputStream();
            //folder path:create
            String cleanFileName=StringUtils.cleanPath(fileName);
            String cleanFolder=StringUtils.cleanPath(DIR);
            Path path=Paths.get(cleanFolder, cleanFileName);
            logger.info("path:{}",path);
            logger.info("contentType:{}",contentType);

            Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);

            //setting up video meta data
            videoEntity.setContentType(contentType);
            videoEntity.setFilePath(path.toString());

            //save into database
            return videoRepository.save(videoEntity);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public VideoEntity getVideo(String videoId) {
       VideoEntity video = videoRepository.findById(Long.valueOf(videoId)).orElseThrow(()->new RuntimeException("Video not found"));
       return video;
    }

    @Override
    public VideoEntity getVideoByTitle(String title) {
        return null;
    }

    @Override
    public List<VideoEntity> getAllVideos() {
        return videoRepository.findAll();
    }
}
