package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.coupon.repository.RedisCouponRepository;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.event.entity.Event;
import com.example.playcation.event.repository.EventRepository;
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
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final CouponUserAtomicService couponUserService;
  private final CouponUserRepository couponUserRepository;
  private final RedisCouponRepository redisCouponRepository;
  private final EventRepository eventRepository;
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

  public CouponResponseDto findCoupon(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    return CouponResponseDto.toDto(coupon);
  }

  public PagingDto<CouponResponseDto> findAllCouponsAndPaging(long eventId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));

    Page<Coupon> couponPage = couponRepository.findByEventId(eventId, pageable);

    List<CouponResponseDto> couponDtoList = couponPage.getContent().stream()
        .map(coupon -> new CouponResponseDto(coupon.getId(), coupon.getName(), coupon.getStock(),
            coupon.getRate(), coupon.getCouponType(), coupon.getIssuedDate(),
            coupon.getValidDays(), coupon.getEvent().getId()))
        .toList();

    return new PagingDto<>(couponDtoList, couponPage.getTotalElements());
  }

  @Transactional
  public CouponResponseDto createAtomicCoupon(CouponRequestDto requestDto) {
    boolean couponExists = couponRepository.existsByNameAndRate(
        requestDto.getName(),
        requestDto.getRate()
    );
    if (couponExists) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATE_COUPON);
    }
    Event event = eventRepository.findByIdOrElseThrow(requestDto.getEventId());
    Coupon coupon = Coupon.builder()
        .name(requestDto.getName())
        .stock(requestDto.getStock())
        .rate(requestDto.getRate())
        .couponType(requestDto.getCouponType())
        .issuedDate(LocalDate.now())
        .validDays(requestDto.getValidDays())
        .event(event)
        .build();

    couponUserService.setCouponCount(coupon.getName(), requestDto.getStock());

    couponRepository.save(coupon);

    return CouponResponseDto.toDto(coupon);
  }

  @Transactional
  public CouponResponseDto createLockCoupon(CouponRequestDto requestDto) {
    boolean couponExists = couponRepository.existsByNameAndRate(
        requestDto.getName(),
        requestDto.getRate()
    );
    if (couponExists) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATE_COUPON);
    }
    Event event = eventRepository.findByIdOrElseThrow(requestDto.getEventId());
    Coupon coupon = Coupon.builder()
        .name(requestDto.getName())
        .stock(requestDto.getStock())
        .rate(requestDto.getRate())
        .couponType(requestDto.getCouponType())
        .issuedDate(LocalDate.now())
        .validDays(requestDto.getValidDays())
        .event(event)
        .build();

    redisCouponRepository.setCouponCount(requestDto.getName(), requestDto.getStock());

    couponRepository.save(coupon);

    return CouponResponseDto.toDto(coupon);
  }

  @Transactional
  public CouponResponseDto updateCoupon(Long couponId, CouponRequestDto requestDto) {
    Coupon newCoupon = couponRepository.findByIdOrElseThrow(couponId);
    Event event = eventRepository.findByIdOrElseThrow(requestDto.getEventId());
    newCoupon.updateCoupon(requestDto, event);

    couponRepository.save(newCoupon);
    couponUserService.setCouponCount(newCoupon.getName(), newCoupon.getStock());
    redisCouponRepository.setCouponCount(newCoupon.getName(), newCoupon.getStock());

    return CouponResponseDto.toDto(newCoupon);
  }

  // 큐에 있는 사용자 목록 가져오기
  private List<String> getUsersFromQueue(String couponName) {

    // Redis ZSet에서 사용자 이메일을 조회
    Set<String> userIdList = redisTemplate.opsForZSet()
        .range("coupon:request:" + couponName, 0, -1);

    // 결과를 List로 변환
    return userIdList != null ? new ArrayList<>(userIdList) : new ArrayList<>();
  }

  // 큐에 있는 사용자들에게 쿠폰 발행
  @Transactional
  public void atomicPublish(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    // 사용자 목록 가져오기
    List<String> userIdList = getUsersFromQueue(coupon.getName());

    List<CouponUser> atomicCouponUsers = new ArrayList<>();
    for (String userId : userIdList) {
      User user = userRepository.findByIdOrElseThrow(Long.valueOf(userId));
      // 쿠폰 발급
      CouponUser couponUser = CouponUser.builder()
          .user(user)
          .coupon(coupon)
          .issuedDate(coupon.getIssuedDate())
          .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
          .eventTitle(coupon.getEvent().getTitle())
          .build();
      atomicCouponUsers.add(couponUser);
    }
    // 한 번에 DB에 저장
    couponUserRepository.saveAll(atomicCouponUsers);

    coupon.updateStock(couponUserService.getRemainingCouponCount(coupon.getName()));
    couponRepository.save(coupon);

    // Redis에서 한 번에 여러 사용자 제거
    redisTemplate.opsForZSet().remove("coupon:request:" + coupon.getName(), userIdList.toArray());
  }

  // 큐에 있는 사용자들에게 쿠폰 발행 (분산락)
  @Transactional
  public void lockPublish(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    // 사용자 목록 가져오기
    List<String> userIdList = redisCouponRepository.getUsersFromQueue(coupon.getName());

    List<CouponUser> couponUsers = new ArrayList<>();

    for (String userId : userIdList) {
      User user = userRepository.findByIdOrElseThrow(Long.valueOf(userId));
      // 쿠폰 발급
      CouponUser couponUser = CouponUser.builder()
          .user(user)
          .coupon(coupon)
          .issuedDate(coupon.getIssuedDate())
          .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
          .eventTitle(coupon.getEvent().getTitle())
          .build();
      couponUsers.add(couponUser);
    }
    // 한 번에 DB에 저장
    couponUserRepository.saveAll(couponUsers);

    coupon.updateStock(redisCouponRepository.getRemainingCouponCount(coupon.getName()));
    couponRepository.save(coupon);

    redisCouponRepository.removeUserFromMap(coupon.getName());
  }
}
