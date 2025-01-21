package com.example.playcation.user.repository;

import ch.qos.logback.core.util.OptionHelper;
import com.example.playcation.user.entity.Point;
import com.example.playcation.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {

  Optional<Point> getPointByUserId(Long userId);
  default Point getPointByUserIdOrElseThrow(Long id){
    return getPointByUserId(id).orElseThrow();
  }
}
