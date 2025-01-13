package com.example.playcation.category.controller;

import com.example.playcation.category.dto.CategoryRequestDto;
import com.example.playcation.category.dto.CategoryResponseDto;
import com.example.playcation.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorys")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto RequestDto) {
    CategoryResponseDto responseDto = categoryService.createCategory(RequestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{categoryId}")
  public ResponseEntity<CategoryResponseDto> findCategory(@PathVariable Long categoryId) {
    CategoryResponseDto responseDto = categoryService.findCategory(categoryId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponseDto>> findAllCategory() {
    List<CategoryResponseDto> responseDto = categoryService.findAllCategory();
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @PatchMapping("/{categoryId}")
  public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequestDto requestDto) {
    CategoryResponseDto responseDto = categoryService.updateCategory(categoryId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{categoryId}")
  public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
    categoryService.deleteCategory(categoryId);
    return new ResponseEntity<>("삭제되었습니다", HttpStatus.OK);
  }
}
