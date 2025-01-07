package com.example.playcation.tag.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@Builder
@Table(name = "tag")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String tagName;

  public Tag(Long id, String tagName) {
    this.id = id;
    this.tagName = tagName;
  }

  public void updateTag(String tagName) {
    this.tagName = tagName;
  }
}
