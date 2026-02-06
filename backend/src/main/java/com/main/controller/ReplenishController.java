package com.main.controller;


import com.main.ResponseMessageWrapper;
import com.main.dto.ReplenishDto;
import com.main.entities.replenish.ReplenishEntity;
import com.main.security.AuthorizeHandler;
import com.main.services.AccountService;
import com.main.services.ReplenishService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReplenishController {
    private final AccountService accountService;
    private final ReplenishService replenishService;
    private final AuthorizeHandler authorizeHandler;

    @GetMapping(
            path = "/api/replenish/get_all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getReplenishes(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final Long accountId = accountService.getAccountId(login);
        if (accountId == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Счет не найден"),
                    HttpStatus.BAD_REQUEST
            );
        }
        List<ReplenishEntity> replenishes = replenishService.getAllReplenishesByAccountId(accountId);
        return new ResponseEntity<>(replenishes, HttpStatus.OK);
    }

    @PostMapping(
            path = "/api/replenish/new",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessageWrapper> auth(
            @Valid @RequestBody ReplenishDto replenishDto,
            HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final Long accountId = accountService.getAccountId(login);
        if (accountId == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Счет не найден"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final boolean isCreatedNewReplenish = replenishService.createNewReplenish(
                accountId,
                replenishDto.getReplenishAmount()
        );
        if (!isCreatedNewReplenish) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось выполнить пополнение счета"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(new ResponseMessageWrapper("Счет успешно пополнен"), HttpStatus.OK);
    }
}
