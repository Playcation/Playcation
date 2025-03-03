package com.example.playcation.user.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.Grade;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.game.entity.Game;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.ion.Decimal;

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

  private String username;

  private String description;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Enumerated(value = EnumType.STRING)
  private Social social;

  @Enumerated(value = EnumType.STRING)
  private Grade grade;

  @JsonFormat(pattern = "yy:MM:dd hh:mm:ss")
  private LocalDateTime deletedAt;

  public User(String email, String password, String name, String username, Role role, Social social) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.username = username;
    this.role = role;
    this.social = social;
    this.grade = Grade.NORMAL;
  }

  public void update(String username, String description, FileDetail fileDetail) {
    this.username = username == null ? this.username : username;
    this.imageUrl = fileDetail == null ? this.imageUrl : fileDetail.getFilePath();
    this.description = description == null ? this.description : description;
  }

  public void updateRole() {
    this.role = Role.MANAGER;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }

  public void updateSocial(Social social) {
    this.social = social;
  }

  public boolean isManagerOfGame(Game game) {
    return this.equals(game.getUser());
  }

  public void updateGrade(Grade grade) {
    this.grade = grade;
  }

  /**
   * 회원 탈퇴 후 30일이 지난 계정을 영구 삭제
   */
  public void expire() {
    this.name = null;
    this.username = null;
    this.password = null;
    this.description = null;
    this.imageUrl = null;
    this.role = null;
    this.social = null;
  }
}