package com.example.playcation.event.dto;

import com.example.playcation.event.entity.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventResponseDto {

  private final Long eventId;

  private final String title;

  private final String description;

  public static EventResponseDto toDto(Event event) {
    return new EventResponseDto(
        event.getId(),
        event.getTitle(),
        event.getDescription()
    );
  }
}
