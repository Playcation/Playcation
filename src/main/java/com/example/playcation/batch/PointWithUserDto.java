package com.example.playcation.batch;

import com.example.playcation.user.entity.Point;
import com.example.playcation.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PointWithUserDto {

  private final Point point;

  private final User user;

}
