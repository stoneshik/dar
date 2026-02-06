package com.main.controller.order.get;

import com.main.ResponseMessageWrapper;
import com.main.entities.file.FileInfoEntity;
import com.main.entities.order.OrderPrintWithFilesInfoEntity;
import com.main.entities.order.OrderScanWithNumberPages;
import com.main.entities.order.OrderWithAddress;
import com.main.security.AuthorizeHandler;
import com.main.services.FileService;
import com.main.services.OrderWithAddressService;
import com.main.services.task.TaskScanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetOrderController {
    private final OrderWithAddressService orderWithAddressService;
    private final FileService fileService;
    private final TaskScanService taskScanService;
    private final AuthorizeHandler authorizeHandler;

    @GetMapping(
            path = "/api/order/get_print/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderPrintById(
            HttpServletRequest httpServletRequest,
            @PathVariable Long orderId) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final OrderWithAddress order = orderWithAddressService.getOrderById(login, orderId);
        if (order == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось получить информацию о заказе"),
                    HttpStatus.BAD_REQUEST
            );
        }
        List<FileInfoEntity> files = fileService.getFilesByOrderId(order.getOrderId());
        if (files == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось получить информацию о файлах приложенных к заказу"),
                    HttpStatus.BAD_REQUEST
            );
        }
        OrderPrintWithFilesInfoEntity ordersInfo = new OrderPrintWithFilesInfoEntity(order, files);
        return new ResponseEntity<>(ordersInfo, HttpStatus.OK);
    }

    @GetMapping(
            path = "/api/order/get_scan/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderScanById(
            HttpServletRequest httpServletRequest,
            @PathVariable Long orderId) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final OrderWithAddress order = orderWithAddressService.getOrderById(login, orderId);
        if (order == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось получить информацию о заказе"),
                    HttpStatus.BAD_REQUEST
            );
        }
        Long numberPages = taskScanService.getScanTaskNumberPagesByOrderId(order.getOrderId());
        if (numberPages == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось получить информацию о количестве заказанных страниц"),
                    HttpStatus.BAD_REQUEST
            );
        }
        OrderScanWithNumberPages ordersInfo = new OrderScanWithNumberPages(order, numberPages);
        return new ResponseEntity<>(ordersInfo, HttpStatus.OK);
    }
}
