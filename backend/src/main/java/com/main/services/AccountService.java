package com.main.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.dto.ReplenishDto;
import com.main.entities.account.BalanceEntity;
import com.main.entities.replenish.ReplenishEntity;
import com.main.repositories.impls.AccountRepositoryImpl;
import com.main.repositories.impls.ReplenishRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepositoryImpl accountRepository;
    private final ReplenishRepositoryImpl replenishService;

    @Transactional
    public ResponseEntity<Object> getBalance(String login) {
        BalanceEntity balanceEntity = accountRepository.getBalance(login);
        if (balanceEntity == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Получить информацию о балансе не получилось"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(balanceEntity, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getReplenishes(String login) {
        final Long accountId = accountRepository.getAccountId(login);
        if (accountId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Счет не найден"),
                HttpStatus.NOT_FOUND
            );
        }
        List<ReplenishEntity> replenishes = replenishService.getAllReplenishesByAccountId(accountId);
        return new ResponseEntity<>(replenishes, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ResponseMessageWrapper> createReplenish(
        ReplenishDto replenishDto,
        String login
    ) {
        if (replenishDto.getReplenishAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Значение пополнения меньше или равно нулю"),
                HttpStatus.BAD_REQUEST
            );
        }
        final Long accountId = accountRepository.getAccountId(login);
        if (accountId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Счет не найден"),
                HttpStatus.NOT_FOUND
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
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Счет успешно пополнен"),
            HttpStatus.OK
        );
    }
}
