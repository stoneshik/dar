package com.main.entities.user;

import java.sql.Date;

import lombok.Data;

@Data
public class UserEntity {
    private Long userId;
    private String userEmail;
    private String userLogin;
    private String userPasswordHash;
    private Date userCreatedDatetime;
    private UserStatus userStatus;

    public UserEntity(
        long userId,
        String userEmail,
        String userLogin,
        String userPasswordHash,
        Date userCreatedDatetime,
        String userStatus
    ) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userLogin = userLogin;
        this.userPasswordHash = userPasswordHash;
        this.userCreatedDatetime = userCreatedDatetime;
        this.userStatus = UserStatus.getValueByName(userStatus);
    }
}
