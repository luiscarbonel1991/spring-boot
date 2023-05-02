package com.food.ordering.order.service.domain;

import com.food.ordering.order.service.domain.entity.Order;
import com.food.ordering.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.order.service.domain.entity.Restaurant;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureReasons);

    void cancelOrder(Order order, List<String> failureReasons);
}
