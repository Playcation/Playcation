package com.example.playcation.category.entity;

import com.example.playcation.category.dto.CategoryRequestDto;
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
@Table(name = "category")
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String categoryName;

  public void categoryUpdate(CategoryRequestDto requestDto) {
    this.categoryName = requestDto.getCategoryName();
  }
}
