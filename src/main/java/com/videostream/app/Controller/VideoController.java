package com.videostream.app.Controller;

import com.videostream.app.Config.AppConstants;
import com.videostream.app.Service.VideoService;
import com.videostream.app.entity.VideoEntity;
import com.videostream.app.payload.CustomMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {



    private Logger logger=org.slf4j.LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;

    @PostMapping
    public ResponseEntity<CustomMessage> create(@RequestParam("file") MultipartFile file,
                                                @RequestParam("title") String title,
                                                @RequestParam("description") String description) {


        VideoEntity video = new VideoEntity();
        video.setTitle(title);
        video.setDescription(description);


       VideoEntity savedVideo= videoService.saveVideo(video, file);
       if(savedVideo!=null){
           return ResponseEntity.ok(new CustomMessage
                   ("Video uploaded successfully",true));
       }else{
           return ResponseEntity.status(500).body
                   (new CustomMessage("Error in uploading video",false));
       }
    }

    //stram video
    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> stream(@PathVariable String videoId) {
        VideoEntity video=videoService.getVideo(videoId);
        logger.info("video:{}",video);
        String contentType=video.getContentType();
        String filePath=video.getFilePath();

        Resource resource=new FileSystemResource(filePath);
        if(contentType==null){
            contentType="application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);

    }

    @GetMapping("/getAllvideo")
    public List<VideoEntity> getAllVideos(){
        return videoService.getAllVideos();
    }

    //stream video in chunks
    @GetMapping("/stream/range/{videoId}")
    public ResponseEntity<Resource> streamVideoChunks(@PathVariable String videoId,
                                                      @RequestHeader(value = "Range", required = false) String rangeHeader)
    {
     logger.info("rangeHeader:{}",rangeHeader);
     VideoEntity video=videoService.getVideo(videoId);
     Path path= Paths.get(video.getFilePath());

     Resource resource= new FileSystemResource(path);
      String contentType=video.getContentType();
      logger.info("contentType:{}",contentType);
      if(contentType==null) {
          contentType = "application/octet-stream";
      }
      long fileLength=path.toFile().length(); //file ki length
      logger.info("fileLength:{}",fileLength);
      //ye if block pehle jaisa hi code hai kyunki range header null hai
      if(rangeHeader==null) {
         return ResponseEntity.ok()
                 .contentType(MediaType.parseMediaType(contentType))
                 .body(resource);
      }else{

          //calculating range start and end
          long rangeStart;
       long rangeEnd;
       String [] ranges=rangeHeader.replace("bytes=", "").split("-");
       rangeStart=Long.parseLong(ranges[0]);

       rangeEnd=rangeStart+ AppConstants.CHUNK_SIZE-1;

       if(rangeEnd>fileLength) {
         rangeEnd=fileLength-1;
       }

//       if(ranges.length>1){
//           rangeEnd=Long.parseLong(ranges[1]);
//       }else{
//           rangeEnd=fileLength-1;
//       }
//       if(rangeEnd>fileLength-1) {
//       rangeEnd=fileLength-1;
//       }


          InputStream inputStream;
       try {

           inputStream = Files.newInputStream(path);
           inputStream.skip(rangeStart);

           long contentLength=rangeEnd-rangeStart+1;

           byte[] data=new byte[(int)contentLength];
           int read = inputStream.read(data, 0, (int) (rangeEnd - rangeStart + 1));
           logger.info("read:{}",read);
           logger.info("rangeStart:{}",rangeStart);
           logger.info("rangeEnd:{}",rangeEnd);
           logger.info("contentLength:{}",contentLength);
           HttpHeaders httpHeaders=new HttpHeaders();
           httpHeaders.add("Content-Range","bytes "+rangeStart+"-"+rangeEnd+"/"+fileLength);
           httpHeaders.add("Cache-Control","no-cache, no-store, must-revalidate");
           httpHeaders.add("Pragma","no-cache");
           httpHeaders.add("Expires","0");
           httpHeaders.add("X-content-Type-Options","nosniff");
           httpHeaders.setContentLength(contentLength);
           return ResponseEntity
                   .status(HttpStatus.PARTIAL_CONTENT)
                   .headers(httpHeaders)
                   .contentType(MediaType.parseMediaType(contentType))
                   .body(new ByteArrayResource(data));

       }catch (IOException e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

       }
      }

    }

}
