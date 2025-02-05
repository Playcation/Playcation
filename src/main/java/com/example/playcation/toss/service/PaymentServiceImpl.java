package com.example.playcation.toss.service;

import com.example.playcation.config.TossPaymentConfig;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InconsistencyException;
import com.example.playcation.exception.InsufficientException;
import com.example.playcation.exception.PaymentErrorCode;
import com.example.playcation.toss.dto.PaymentSuccessDto;
import com.example.playcation.toss.entity.Payment;
import com.example.playcation.toss.repository.PaymentRepository;
import com.example.playcation.user.entity.Point;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.PointRepository;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final UserRepository userRepository;
  private final PointRepository pointRepository;
  private final PaymentRepository paymentRepository;
  private final TossPaymentConfig tossPaymentConfig;

  @Override
  public Payment requestTossPayment(Payment payment, Long userId) {
    User user = userRepository.findByIdOrElseThrow(userId);
    payment.updateUser(user);
    return paymentRepository.save(payment);
  }
  @Override
  public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId,
      BigDecimal amount) {
    Payment payment = verifyPayment(orderId, amount);
    PaymentSuccessDto result = requestPaymentAccept(paymentKey, orderId, amount);
    payment.updatePaymentKey(paymentKey);
    payment.updatePaySuccess(true);
    // 포인트 증가
    Point point = pointRepository.getPointByUserIdOrElseThrow(payment.getUser().getId());
    point.updatePaidPoint(point.getPaidPoint().add(amount));
    // 유저가 보유한 금액 증가(결제 -> 충전 -> 구매로 흘러갈 시 사용)
    userRepository.save(payment.getUser());
    return result;
  }

  // 결제 요청됭 금액과 실제 결제된 금액이 같은지 검증
  public Payment verifyPayment(String orderId, BigDecimal amount) {
    Payment payment = paymentRepository.findByOrderIdOrElseThrow(orderId);
    if (!payment.getAmount().equals(amount)) {
      throw new InconsistencyException(PaymentErrorCode.PAYMENT_AMOUNT_EXP);
    }
    return payment;
  }

  @Transactional
  public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId,  BigDecimal amount) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = getHeaders();
    JSONObject params = new JSONObject();
    params.put("orderId", orderId);
    params.put("amount", amount);
    PaymentSuccessDto result = null;
    try {
      result = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey,
          new HttpEntity<>(params, headers),
          PaymentSuccessDto.class);
    } catch (Exception e) {
      throw new DuplicatedException(PaymentErrorCode.ALREADY_APPROVED);
    }
    return result;
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    String encodedAuthKey = new String(
        Base64.getEncoder().encode((tossPaymentConfig.getTestSecreteKey() + ":").getBytes(
            StandardCharsets.UTF_8)));
    headers.setBasicAuth(encodedAuthKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    return headers;
  }

  public void tossPaymentFail(String code, String message, String orderId) {
    Payment payment = paymentRepository.findByOrderIdOrElseThrow(orderId);
    payment.updatePaySuccess(false);
    payment.updateFailReason(message);
  }

  public Map cancelPaymentPoint(Long userId, String paymentKet, String cancelReason) {
    User user = userRepository.findByIdOrElseThrow(userId);
    Payment payment = paymentRepository.findByPaymentKeyAndUser_EmailOrElseThrow(paymentKet, user.getEmail());
    Point point = pointRepository.getPointByUserIdOrElseThrow(userId);
    if (point.getPaidPoint().compareTo(payment.getAmount()) > 0) {
      payment.updateCancelYN(true);
      payment.updateCancelReason(cancelReason);
      point.updatePaidPoint( point.getPaidPoint().subtract(payment.getAmount()));
      return tossPaymentCancel(paymentKet, cancelReason);
    }
    throw new InsufficientException(PaymentErrorCode.PAYMENT_NOT_ENOUGH_POINT);
  }

  public Map tossPaymentCancel(String paymentKey, String cancelReason) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = getHeaders();
    JSONObject params = new JSONObject();
    params.put("cancelReason", cancelReason);
    return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
        new HttpEntity<>(params, headers),
        Map.class);
  }

  public Page<Payment> findAllChargingHistories(Long userId, int page) {
    User user = userRepository.findByIdOrElseThrow(userId);
    return paymentRepository.findAllByUser(user, PageRequest.of(page, 10, Sort.by(Direction.DESC, "paymentId")));
  }
}
