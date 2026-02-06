package com.main.controller;

import com.main.ResponseMessageWrapper;
import com.main.entities.account.BalanceEntity;
import com.main.security.AuthorizeHandler;
import com.main.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AuthorizeHandler authorizeHandler;

    @GetMapping(
            path = "/api/account/get_balance",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getBalance(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        BalanceEntity balanceEntity = accountService.getBalance(login);
        if (balanceEntity == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Получить информацию о балансе не получилось"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(balanceEntity, HttpStatus.OK);
    }
}
