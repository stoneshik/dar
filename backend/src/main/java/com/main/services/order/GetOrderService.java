package com.main.services.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.entities.file.FileInfoEntity;
import com.main.entities.order.OrderPrintWithFilesInfoEntity;
import com.main.entities.order.OrderScanWithNumberPages;
import com.main.entities.order.OrderStatus;
import com.main.entities.order.OrderWithAddress;
import com.main.repositories.impls.FileRepositoryImpl;
import com.main.repositories.impls.OrderWithAddressRepositoryImpl;
import com.main.repositories.impls.task.TaskScanRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetOrderService {
    private final OrderWithAddressRepositoryImpl orderWithAddressRepository;
    private final FileRepositoryImpl fileRepository;
    private final TaskScanRepositoryImpl taskScanRepository;

    @Transactional
    public ResponseEntity<Object> getOrderPrintById(String login, Long orderId) {
        final OrderWithAddress order = orderWithAddressRepository.getOrderById(login, orderId);
        if (order == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Заказ не найден"),
                HttpStatus.NOT_FOUND
            );
        }
        List<FileInfoEntity> files = fileRepository.getFilesByOrderId(order.getOrderId());
        if (files == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о файлах приложенных к заказу"),
                HttpStatus.BAD_REQUEST
            );
        }
        OrderPrintWithFilesInfoEntity ordersInfo = new OrderPrintWithFilesInfoEntity(order, files);
        return new ResponseEntity<>(ordersInfo, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getOrderScanById(String login, Long orderId) {
        final OrderWithAddress order = orderWithAddressRepository.getOrderById(login, orderId);
        if (order == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Заказ не найден"),
                HttpStatus.NOT_FOUND
            );
        }
        Long numberPages = taskScanRepository.getScanTaskNumberPagesByOrderId(order.getOrderId());
        if (numberPages == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о количестве заказанных страниц"),
                HttpStatus.BAD_REQUEST
            );
        }
        OrderScanWithNumberPages ordersInfo = new OrderScanWithNumberPages(order, numberPages);
        return new ResponseEntity<>(ordersInfo, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getOrders(String login, OrderStatus orderStatus) {
        final List<OrderWithAddress> orders = getOrdersFromBd(login, orderStatus);
        if (orders == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о заказах"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    public List<OrderWithAddress> getOrdersFromBd(String login, OrderStatus orderStatus) {
        List<OrderWithAddress> orders;
        switch (orderStatus) {
            case PAID -> orders = orderWithAddressRepository.getPaidOrders(login);
            case NOT_PAID -> orders = orderWithAddressRepository.getNotPaidOrders(login);
            default -> orders = null;
        }
        return orders;
    }
}
