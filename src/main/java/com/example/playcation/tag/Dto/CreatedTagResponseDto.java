package com.example.playcation.tag.Dto;

import com.example.playcation.tag.entity.Tag;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreatedTagResponseDto {

  private Long tagId;

  private String tagName;

  public CreatedTagResponseDto(Long tagId, String tagName) {
    this.tagId = tagId;
    this.tagName = tagName;
  }

  public static CreatedTagResponseDto toDto(Tag tag) {
    return new CreatedTagResponseDto(
        tag.getId(),
        tag.getTagName());
  }
}
