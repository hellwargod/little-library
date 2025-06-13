package com.markpen.library.model.dto.user;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3125L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
