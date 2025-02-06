package com.example.playcation.event.repository;

import com.example.playcation.event.entity.Event;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


  default Event findByIdOrElseThrow(Long id) {
    Event event = findById(id).orElseThrow(
        () -> new NotFoundException(CouponErrorCode.EVENT_NOT_FOUND));

    return event;
  }

  boolean existsByTitle(String title);
}