package com.example.playcation.user.entity;

import com.example.playcation.enums.Role;
import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.Social;
import com.example.playcation.s3.entity.FileDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`user`")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 100)
  private String email;

  @Column(length = 100)
  private String password;

  private String imageUrl;

  @Column(length = 10)
  private String name;

  private String description;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Enumerated(value = EnumType.STRING)
  private Social social;

  @JsonFormat(pattern = "yy:MM:dd hh:mm:ss")
  private LocalDateTime deletedAt;

  public User(String email, String password, String name, Role role, Social social) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.role = role;
    this.social = social;
  }

  public void update(String name, String description, FileDetail fileDetail) {
    this.name = name == null ? this.name : name;
    this.imageUrl = fileDetail == null ? this.imageUrl : fileDetail.getFilePath();
    this.description = description == null ? this.description : description;
  }

  public void updateRole(){
    this.role = Role.MANAGER;
  }

  public void updatePassword(String password){
    this.password = password;
  }

  public void delete(){
    this.deletedAt = LocalDateTime.now();
  }

  public void updateSocial(Social social){
    this.social = social;
  }
}