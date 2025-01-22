package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponAdminService {

  private final UserRepository userRepository;
  private final CouponRepository couponRepository;
  private final CouponUserRepository couponUserRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final RedisTemplate<String, String> redisTemplate;

  @Transactional
  public void createTestUsers(Long userAmount) {
    for (int i = 1; i <= userAmount; i++) {
      userRepository.save(
          new User("email" + i + "@example.com", bCryptPasswordEncoder.encode("q1w2e3r4!!"),
              "name" + i, "username" + i, Role.USER,
              Social.NORMAL));
    }
  }

  @Transactional
  public CouponResponseDto createCoupon(CouponRequestDto requestDto) {
    boolean couponExists = couponRepository.existsByNameAndRate(
        requestDto.getName(),
        requestDto.getRate()
    );

    if (couponExists) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATE_COUPON);
    }
    Coupon coupon = Coupon.builder()
        .name(requestDto.getName())
        .stock(requestDto.getStock())
        .rate(requestDto.getRate())
        .couponType(requestDto.getCouponType())
        .issuedDate(LocalDate.now())
        .validDays(requestDto.getValidDays())
        .build();

    setCouponCount(coupon.getId(), requestDto.getStock());

    couponRepository.save(coupon);

    return CouponResponseDto.toDto(coupon);
  }

  public CouponResponseDto findCoupon(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    return CouponResponseDto.toDto(coupon);
  }

  public PagingDto<CouponResponseDto> findAllCouponsAndPaging(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));

    Page<Coupon> couponPage = couponRepository.findAll(pageable);

    List<CouponResponseDto> couponDtoList = couponPage.getContent().stream()
        .map(coupon -> new CouponResponseDto(coupon.getId(), coupon.getName(), coupon.getStock(),
            coupon.getRate(), coupon.getCouponType(), coupon.getIssuedDate(),
            coupon.getValidDays()))
        .toList();

    return new PagingDto<>(couponDtoList, couponPage.getTotalElements());
  }

  @Transactional
  public CouponResponseDto updateCoupon(Long couponId, CouponRequestDto requestDto) {
    Coupon newCoupon = couponRepository.findByIdOrElseThrow(couponId);

    newCoupon.updateCoupon(requestDto);

    couponRepository.save(newCoupon);

    return CouponResponseDto.toDto(newCoupon);
  }

  // 큐에 있는 사용자들에게 쿠폰 발행
  public void publish(Coupon coupon) {
    List<User> users = getUsersFromQueue(coupon);
    for (User user : users) {
      issueCoupon(coupon, user);
      decrementCouponCount(coupon);
      redisTemplate.opsForZSet().remove(coupon.getName(), user.getEmail());
    }
  }

  // 이벤트 쿠폰 수량 설정
  public void setCouponCount(Long couponId, long count) {
    redisTemplate.opsForValue().set(couponId + "_COUNT", String.valueOf(count));
  }

  // 이벤트 쿠폰 수량 감소
  public void decrementCouponCount(Coupon coupon) {
    redisTemplate.opsForValue().decrement(coupon.getId() + "_COUNT");
  }

  // 큐에 있는 사용자 목록 가져오기
  private List<User> getUsersFromQueue(Coupon coupon) {
    Set<String> emails = redisTemplate.opsForZSet().range(coupon.getName(), 0, -1);
    List<User> users = new ArrayList<>();
    if (emails != null) {
      for (String email : emails) {
        userRepository.findByEmail(email).ifPresent(users::add);
      }
    }
    return users;
  }

  // 사용자에게 쿠폰 발행
  public void issueCoupon(Coupon coupon, User user) {
    // 쿠폰 발급
    CouponUser couponUser = CouponUser.builder()
        .user(user)
        .coupon(coupon)
        .issuedDate(coupon.getIssuedDate())
        .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
        .build();

    couponUserRepository.save(couponUser);
    log.info("'{}'에게 {} 쿠폰이 발급되었습니다", user.getEmail(), coupon.getName());
  }
}
