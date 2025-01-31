package com.example.playcation.redis.distributedLock;

import com.example.playcation.exception.InternalServerException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.LockErrorCode;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

  private static final String REDISSON_LOCK_PREFIX = "LOCK:";

  private final RedissonClient redissonClient;
  private final AopForTransaction aopForTransaction;

  @Around("@annotation(com.example.playcation.redis.distributedLock.DistributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

    String key =
        REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
            joinPoint.getArgs(), distributedLock.key());
    log.info("lock on [method: {}] [key:{}", method, key);
    RLock rLock = redissonClient.getLock(key);  // (1)
    String lockName = rLock.getName();
    try {
      boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
          distributedLock.timeUnit());  // (2)

      if (!available) {
        throw new InvalidInputException(LockErrorCode.LOCK_NOT_AVAILABLE);
      }

      return aopForTransaction.proceed(joinPoint);  // (3)

    } catch (InterruptedException e) {
      throw new InternalServerException(LockErrorCode.LOCK_INTERRUPTED_ERROR);
    } finally {
      try {
        rLock.unlock();   // (4)
        log.info("unlock complete [Lock:{}]", lockName);
      } catch (IllegalMonitorStateException e) {
        log.info("Redisson Lock Already UnLocked");
      }
    }
  }
}