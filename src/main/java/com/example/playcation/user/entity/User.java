package com.example.playcation.user.entity;

import com.example.playcation.common.Auth;
import com.example.playcation.common.BaseEntity;
import com.example.playcation.util.PasswordEncoder;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`user`")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 100)
  private String email;

  @Convert(converter = PasswordEncoder.class)
  @Column(length = 100)
  private String password;

  @Column(length = 10)
  private String name;

  @Enumerated(value = EnumType.STRING)
  private Auth auth;

  @JsonFormat(pattern = "yy:MM:dd hh:mm:ss")
  private LocalDateTime deletedAt;

  public User(String email, String password, String name, Auth auth) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.auth = auth;
  }

  public void delete(){
    this.deletedAt = LocalDateTime.now();
  }
}
