package com.example.playcation.event.dto;

import lombok.Getter;

@Getter
public class EventRequestDto {

  private String title;

  private String description;

  public EventRequestDto(String title, String description) {
    this.title = title;
    this.description = description;
  }
}
