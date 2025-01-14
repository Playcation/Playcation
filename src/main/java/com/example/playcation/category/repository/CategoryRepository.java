package com.example.playcation.category.repository;

import com.example.playcation.category.entity.Category;
import com.example.playcation.exception.CategoryErrorCode;
import com.example.playcation.exception.GameTagErrorCode;
import com.example.playcation.exception.NotFoundException;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  default Category findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(CategoryErrorCode.NOT_FOUND_CATEGORY));
  }

  boolean existsCategoryByCategoryName(String categoryName);
}
