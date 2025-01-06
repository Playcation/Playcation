package com.example.playcation.s3.repository;

import com.example.playcation.s3.entity.PhotoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoFile, Long> {

}
