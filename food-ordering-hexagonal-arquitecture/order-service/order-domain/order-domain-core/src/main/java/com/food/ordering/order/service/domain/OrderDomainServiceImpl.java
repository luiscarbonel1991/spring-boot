package com.food.ordering.order.service.domain;

import com.food.ordering.order.service.domain.entity.Order;
import com.food.ordering.order.service.domain.entity.Product;
import com.food.ordering.order.service.domain.entity.Restaurant;
import com.food.ordering.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {
    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id {} is initialized", order.getId().getValue());
        return new OrderCreatedEvent(order, getUtc());
    }


    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order, getUtc());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureReasons) {
        order.initCancel(failureReasons);
        return new OrderCancelledEvent(order, getUtc());
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id {} is approved", order.getId().getValue());
    }


    @Override
    public void cancelOrder(Order order, List<String> failureReasons) {
        order.cancel(failureReasons);
        log.info("Order with id {} is cancelled", order.getId().getValue());
    }
    private static ZonedDateTime getUtc() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive())
            throw new OrderDomainException("Restaurant is not available");
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        order.getOrderItems().forEach(orderItem -> restaurant.getProducts().forEach(restaurantProduct -> {
            Product currentProduct = orderItem.getProduct();
            if (currentProduct.equals(restaurantProduct)) {
                currentProduct.updateWithConfirmedNameAndPrice(
                        restaurantProduct.getName(),
                        restaurantProduct.getPrice()
                );
            }
        }));
    }
}
