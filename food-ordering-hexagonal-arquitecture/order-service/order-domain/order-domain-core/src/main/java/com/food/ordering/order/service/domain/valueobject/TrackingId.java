package com.food.ordering.order.service.domain.valueobject;

import com.food.ordering.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {

    public TrackingId(UUID value) {
        super(value);
    }


}
