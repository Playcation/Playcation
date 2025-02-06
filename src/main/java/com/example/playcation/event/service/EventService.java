package com.example.playcation.event.service;

import com.example.playcation.event.dto.EventRequestDto;
import com.example.playcation.event.dto.EventResponseDto;
import com.example.playcation.event.entity.Event;
import com.example.playcation.event.repository.EventRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  @Transactional
  public EventResponseDto createEvent(EventRequestDto requestDto) {
    // name 중복 체크
    if (eventRepository.existsByTitle(requestDto.getTitle())) {
      throw new DuplicatedException(CouponErrorCode.EVENT_NOT_FOUND);
    }

    Event newEvent = Event.builder()
        .title(requestDto.getTitle())
        .description(requestDto.getDescription())
        .build();

    // 변경된 장바구니 저장
    eventRepository.save(newEvent);

    return EventResponseDto.toDto(newEvent);
  }

  public EventResponseDto findEvent(Long eventId) {
    Event event = eventRepository.findByIdOrElseThrow(eventId);

    return EventResponseDto.toDto(event);
  }

  public List<EventResponseDto> findAllEvents() {

    List<Event> eventList = eventRepository.findAll();
    return eventList.stream()
        .map(event -> new EventResponseDto(
            event.getId(),
            event.getTitle(),
            event.getDescription()
        ))
        .toList();
  }
}