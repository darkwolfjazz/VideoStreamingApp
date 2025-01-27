package com.videostream.app.repository;

import com.videostream.app.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity,Long> {

Optional<VideoEntity> findByTitle(String title);



}
