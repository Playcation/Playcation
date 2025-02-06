package com.example.playcation.event.controller;

import com.example.playcation.event.dto.EventRequestDto;
import com.example.playcation.event.dto.EventResponseDto;
import com.example.playcation.event.service.EventService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventAdminController {

  private final EventService eventService;

  @PostMapping
  public ResponseEntity<EventResponseDto> createEvent(
      @Valid @RequestBody EventRequestDto requestDto) {
    EventResponseDto responseDto = eventService.createEvent(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<EventResponseDto> findEvent(@PathVariable Long eventId) {
    EventResponseDto responseDto = eventService.findEvent(eventId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<EventResponseDto>> findAllEvents() {
    List<EventResponseDto> events = eventService.findAllEvents();
    return new ResponseEntity<>(events, HttpStatus.OK);
  }
}
