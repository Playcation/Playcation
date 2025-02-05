package com.example.playcation.coupon.repository;

import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCouponRepository {

  private final RedissonClient redissonClient;
  private final String COUPON_COUNT_MAP = "CouponCountMap";
  private final String COUPON_REQUEST_USER_MAP = "CouponRequestUserMap";
  private final String KEY_COUPON_HEADER = "COUPON:";


  public void addUser(Long userId, String couponName) {
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        new StringCodec());

    // 사용자 추가 (자동으로 여러 개 저장됨)
    userMap.put(getCouponKeyString(couponName), userId.toString());

  }

  public void findUserFromQueue(Long userId, String couponName) {
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        new StringCodec());

    // 이미 쿠폰을 요청한 사용자 여부 확인
    if (userMap.get(getCouponKeyString(couponName)).contains(userId.toString())) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATED_REQUESTED_COUPON);
    }
  }

  public List<String> getUsersFromQueue(String couponName) {
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        new StringCodec());

    // 특정 쿠폰의 모든 사용자 가져오기 (Set<String>)
    Set<String> userSet = userMap.get(getCouponKeyString(couponName));
    // Set을 List로 변환하여 반환
    return new ArrayList<>(userSet);
  }

  public void removeUserFromMap(String couponName) {
    // RMap 가져오기
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        new StringCodec());

    userMap.removeAll(getCouponKeyString(couponName));
  }

  // 쿠폰 수량 설정
  public void setCouponCount(String couponName, Long count) {
    RMap<String, Long> countMap = redissonClient.getMap(COUPON_COUNT_MAP, new LongCodec());
    countMap.put(getCouponKeyString(couponName), count);
  }

  // 남은 쿠폰 수량 가져오기
  public long getRemainingCouponCount(String couponName) {
    RMap<String, Long> countMap = redissonClient.getMap(COUPON_COUNT_MAP, new LongCodec());
    return countMap.get(getCouponKeyString(couponName));
  }

  // 쿠폰 수량 감소
  public void decrementCouponCount(String couponName) {
    RMap<String, Long> countMap = redissonClient.getMap(COUPON_COUNT_MAP, new LongCodec());
    countMap.addAndGet(getCouponKeyString(couponName), -1);
  }

  private String getCouponKeyString(String couponName) {
    return new StringBuilder()
        .append(KEY_COUPON_HEADER)
        .append(couponName)
        .toString();
  }


}
