package com.videostream.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Course {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String title;



}
