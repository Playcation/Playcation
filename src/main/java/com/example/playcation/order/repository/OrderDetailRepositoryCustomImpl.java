package com.example.playcation.order.repository;

import com.example.playcation.batch.job.GameProfitDto;
import com.example.playcation.order.entity.QOrderDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderDetailRepositoryCustomImpl implements OrderDetailCustomRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public BigDecimal settleProfit(Long managerId) {

    QOrderDetail orderDetail = QOrderDetail.orderDetail;

    List<GameProfitDto> profit = queryFactory
        .select(Projections.constructor(
            GameProfitDto.class,
            orderDetail.game.title,
            orderDetail.price.sum()
        ))
        .from(orderDetail)
        .where(
            eqUserId(managerId)
        )
        .groupBy(QOrderDetail.orderDetail.game)
        .fetch();

    return null;
  }

  private BooleanExpression eqUserId(Long id) {
    return id != null ? QOrderDetail.orderDetail.game.user.id.eq(id) : null;
  }

  /*
  TODO: 게임별 수익금을 계산
    1. OrderDetail 생성일이 2일 지났는지(환불 불가한지)
    2. 이미 정산된 주문 내역은 제외(is_Settled)
   */
}
