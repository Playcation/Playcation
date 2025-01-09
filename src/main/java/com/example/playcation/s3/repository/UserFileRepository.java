package com.example.playcation.s3.repository;

import com.example.playcation.s3.entity.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileRepository extends JpaRepository<UserFile, Long> {

}
