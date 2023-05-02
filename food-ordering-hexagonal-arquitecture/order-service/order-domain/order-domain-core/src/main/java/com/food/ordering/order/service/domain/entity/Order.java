package com.food.ordering.order.service.domain.entity;

import com.food.ordering.domain.entity.AggregateRoot;
import com.food.ordering.domain.valueobject.*;
import com.food.ordering.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.order.service.domain.exception.OrderDomainException;
import com.food.ordering.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;

    private final StreetAddress streetAddress;

    private final Money price;
    private final List<OrderItem> orderItems;

    private TrackingId trackingId;

    private OrderStatus orderStatus;

    private List<String> failureMessages;

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder() {
        if (orderStatus != null && getId() != null) {
            throw new OrderDomainException("Order is not in initial state");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total prince must be greater than zero");
        }
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in pending state");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in paid state");
        }
        orderStatus = OrderStatus.APPROVED;
    }
    
    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in paid state to be cancelled");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.CANCELLING && orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in cancelling state");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (failureMessages != null && !failureMessages.isEmpty()) {
            if(this.failureMessages == null)
                this.failureMessages = failureMessages.stream().filter(message -> message != null && !message.isEmpty()).toList();
            else
                this.failureMessages.addAll(failureMessages.stream().filter(message -> message != null && !message.isEmpty()).toList());
        }
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = orderItems.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!orderItemsTotal.equals(price)) {
            throw new OrderDomainException("Total price is not equal to order items total price");
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price is not valid. Price: " + orderItem.getPrice() + " SubTotal: " + orderItem.getSubTotal());
        }
    }


    private void initializeOrderItems() {
        AtomicLong orderItemId = new AtomicLong(1);
        orderItems.forEach(orderItem ->
                orderItem.initializeOrderItem(super.getId(),
                        new OrderItemId(orderItemId.getAndIncrement()))
        );
    }

    private Order(Builder builder) {
        super.setId(builder.id);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        streetAddress = builder.streetAddress;
        price = builder.price;
        orderItems = builder.orderItems;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }


    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId id;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress streetAddress;
        private Money price;
        private List<OrderItem> orderItems;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder orderId(OrderId val) {
            id = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder streetAddress(StreetAddress val) {
            streetAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder orderItems(List<OrderItem> val) {
            orderItems = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
