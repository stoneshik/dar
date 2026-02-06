package com.main.repositories;

import com.main.entities.user.UserEntity;


public interface UserRepository {
    int create(String email, String login, String password);
    UserEntity findByLogin(String login);

    UserEntity findByEmail(String email);

    Long getUserIdByLogin(String login);
}
