package com.main.controller.order.remove;


import com.main.ResponseMessageWrapper;
import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;
import com.main.entities.order.OrderStatus;
import com.main.security.AuthorizeHandler;
import com.main.services.AccountService;
import com.main.services.FileService;
import com.main.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderRemoveController {
    private final OrderService orderService;
    private final AccountService accountService;
    private final FileService fileService;
    private final AuthorizeHandler authorizeHandler;

    private OrderRemoveStrategy switchOrderRemoveStrategy(OrderStatus orderStatus) {
        OrderRemoveStrategy orderRemoveStrategy;
        switch (orderStatus) {
            case PAID -> orderRemoveStrategy = new OrderPaidRemoveStrategy(orderService, accountService, fileService);
            case NOT_PAID -> orderRemoveStrategy = new OrderNotPaidRemoveStrategy(orderService, fileService);
            case COMPLETED -> orderRemoveStrategy = new OrderCompletedRemoveStrategy();
            default -> orderRemoveStrategy = null;
        }
        return orderRemoveStrategy;
    }

    @DeleteMapping(path = "/api/order/remove/{id}")
    public ResponseEntity<ResponseMessageWrapper> removeOrder(
            @PathVariable("id") Long orderId,
            HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        BalanceEntity balanceEntity = accountService.getBalance(login);
        OrderEntity orderEntity = orderService.getById(balanceEntity.getAccountId(), orderId);
        if (orderEntity == null) {
            return new ResponseEntity<>(new ResponseMessageWrapper("Заказ не найден"), HttpStatus.NOT_FOUND);
        }
        OrderRemoveStrategy orderRemoveStrategy = switchOrderRemoveStrategy(orderEntity.getOrderStatus());
        if (!orderRemoveStrategy.remove(orderEntity, balanceEntity)) {
            return new ResponseEntity<>(new ResponseMessageWrapper("Не получилось удалить заказ"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessageWrapper("Успешное удаление"), HttpStatus.OK);
    }
}
