package com.main.repositories;

import com.main.entities.order.OrderWithAddress;

import java.util.List;

public interface OrderWithAddressRepository {
    List<OrderWithAddress> getPaidOrders(String login);
    List<OrderWithAddress> getNotPaidOrders(String login);

    OrderWithAddress getOrderById(String login, Long orderId);

    boolean checkOrderInfo(OrderWithAddress unverifiedOrderWithAddress);

    boolean changeStatusOrderToPaid(Long orderId);

    boolean changeStatusOrderToCompleted(Long orderId);
}
