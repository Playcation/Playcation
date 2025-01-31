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
  public CouponResponseDto createAtomicCoupon(CouponRequestDto requestDto) {
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
    Coupon coupon = Coupon.builder()
        .name(requestDto.getName())
        .stock(requestDto.getStock())
        .rate(requestDto.getRate())
        .couponType(requestDto.getCouponType())
        .issuedDate(LocalDate.now())
        .validDays(requestDto.getValidDays())
        .build();

    redisCouponRepository.setCouponCount(requestDto.getName(), requestDto.getStock());

    couponRepository.save(coupon);

    return CouponResponseDto.toDto(coupon);
  }

  @Transactional
  public CouponResponseDto updateCoupon(Long couponId, CouponRequestDto requestDto) {
    Coupon newCoupon = couponRepository.findByIdOrElseThrow(couponId);

    newCoupon.updateCoupon(requestDto);

    couponRepository.save(newCoupon);
    couponUserService.setCouponCount(newCoupon.getName(), newCoupon.getStock());
    redisCouponRepository.setCouponCount(newCoupon.getName(), newCoupon.getStock());

    return CouponResponseDto.toDto(newCoupon);
  }

  // 해당 쿠폰을 이미 받았는지 확인
  public boolean canIssueCoupon(Long userId, Long couponId) {
    return couponUserRepository.findByUserIdAndCouponId(userId, couponId).isEmpty();
  }

  // 사용자에게 쿠폰 발행
  @Transactional
  public void issueCoupon(Long userId, Long couponId) {
    User user = userRepository.findByIdOrElseThrow(userId);
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    if (!canIssueCoupon(userId, couponId)) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATE_ISSUED_COUPON);
    }
    // 쿠폰 발급
    CouponUser couponUser = CouponUser.builder()
        .user(user)
        .coupon(coupon)
        .issuedDate(coupon.getIssuedDate())
        .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
        .build();

    couponUserRepository.save(couponUser);
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
  public void atomicPublish(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    int updatedStock = couponUserService.getRemainingCouponCount(coupon.getName());
    coupon.updateStock(updatedStock);

    List<String> userIdList = getUsersFromQueue(coupon.getName());
    for (String userId : userIdList) {
      issueCoupon(Long.valueOf(userId), couponId);
      redisTemplate.opsForZSet().remove("coupon:request:" + coupon.getName(), userId);
    }
  }

  // 큐에 있는 사용자들에게 쿠폰 발행 (분산락)
  public void lockPublish(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    long updatedStock = redisCouponRepository.getRemainingCouponCount(coupon.getName());
    coupon.updateStock(updatedStock);

    List<String> userIdList = redisCouponRepository.getUsersFromQueue(coupon.getName());
    for (String userId : userIdList) {
      issueCoupon(Long.valueOf(userId), couponId);
    }
    redisCouponRepository.removeUserFromMap(coupon.getName());
  }
}
