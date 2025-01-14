package com.example.playcation.category.service;

import com.example.playcation.category.dto.CategoryRequestDto;
import com.example.playcation.category.dto.CategoryResponseDto;
import com.example.playcation.category.entity.Category;
import com.example.playcation.category.repository.CategoryRepository;
import com.example.playcation.exception.CategoryErrorCode;
import com.example.playcation.exception.DuplicatedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
    if(categoryRepository.existsCategoryByCategoryName(requestDto.getCategoryName())) {
      throw new DuplicatedException(CategoryErrorCode.DUPLICATE_CATEGORY);
    }
    Category category = Category.builder()
        .categoryName(requestDto.getCategoryName())
        .build();
    categoryRepository.save(category);
    return CategoryResponseDto.toDto(category);
  }

  public CategoryResponseDto findCategory(Long categoryId) {
    Category category = categoryRepository.findByIdOrElseThrow(categoryId);
    return CategoryResponseDto.toDto(category);
  }

  public List<CategoryResponseDto> findAllCategory() {
    List<CategoryResponseDto> categoryList = categoryRepository.findAll().stream().map(CategoryResponseDto::toDto).toList();
    return categoryList;
  }

  public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto) {
    Category category = categoryRepository.findByIdOrElseThrow(categoryId);
    category.categoryUpdate(requestDto);
    categoryRepository.save(category);
    return CategoryResponseDto.toDto(category);
  }

  public void deleteCategory(Long categoryId) {
    Category category = categoryRepository.findByIdOrElseThrow(categoryId);
    categoryRepository.delete(category);
  }
}
