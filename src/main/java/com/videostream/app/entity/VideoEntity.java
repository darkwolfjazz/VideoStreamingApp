package com.videostream.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VideoEntity {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long videoId;
private String title;
private String description;
private String contentType;
private String filePath;



}
