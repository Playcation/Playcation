package com.example.playcation.notification.repository;

import com.example.playcation.notification.entity.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  Optional<Notification> findById(Long id);

  List<Notification> findAllByReviewIdIn(List<Long> reviewIdList);
}