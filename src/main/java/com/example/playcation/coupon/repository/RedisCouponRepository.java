package com.example.playcation.coupon.repository;

import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RedissonClient;
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
        StringCodec.INSTANCE);

    // 사용자 추가 (자동으로 여러 개 저장됨)
    userMap.put(getCouponKeyString(couponName), userId.toString());

  }

  public void findUserFromQueue(Long userId, String couponName) {
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        StringCodec.INSTANCE);

    // 이미 쿠폰을 요청한 사용자 여부 확인
    if (userMap.get(getCouponKeyString(couponName)).contains(userId.toString())) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATED_REQUESTED_COUPON);
    }
  }

  public List<String> getUsersFromQueue(String couponName) {
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        StringCodec.INSTANCE);

    // 해당 Key가 존재하는지 확인
    if (!userMap.containsKey(getCouponKeyString(couponName)) || userMap.get(
        getCouponKeyString(couponName)).isEmpty()) {
      throw new NotFoundException(CouponErrorCode.REQUEST_USER_NOT_FOUND);
    }

    // 특정 쿠폰의 모든 사용자 가져오기 (Set<String>)
    Set<String> userSet = userMap.get(getCouponKeyString(couponName));
    // Set을 List로 변환하여 반환
    return new ArrayList<>(userSet);
  }

  public void removeUserFromMap(String couponName) {
    // RMap 가져오기
    RSetMultimap<String, String> userMap = redissonClient.getSetMultimap(COUPON_REQUEST_USER_MAP,
        StringCodec.INSTANCE);

    userMap.removeAll(getCouponKeyString(couponName));
  }

  // 쿠폰 수량 설정
  public void setCouponCount(String couponName, Long count) {
    RAtomicLong stock = redissonClient.getAtomicLong("coupon:count:" + couponName);
    stock.set(count);
  }

  // 남은 쿠폰 수량 가져오기
  public long getRemainingCouponCount(String couponName) {
    RAtomicLong stock = redissonClient.getAtomicLong("coupon:count:" + couponName);
    return stock.get();
  }

  public void decrementAndGetCouponCount(String couponName) {
    RAtomicLong stock = redissonClient.getAtomicLong("coupon:count:" + couponName);
    // **원자적으로 감소 **
    stock.decrementAndGet();
  }

  private String getCouponKeyString(String couponName) {
    return new StringBuilder()
        .append(KEY_COUPON_HEADER)
        .append(couponName)
        .toString();
  }


}
